package fr.syrows.smartcommands.commands.issuers;

import fr.syrows.smartcommands.commands.CommandContents;
import fr.syrows.smartcommands.SmartCommand;
import fr.syrows.smartcommands.contents.CommandHelp;
import fr.syrows.smartcommands.contents.CommandUsage;
import fr.syrows.smartcommands.contents.usages.Usage;
import fr.syrows.smartcommands.tools.Pagination;
import fr.syrows.smartcommands.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandIssuer {

    private CommandSender sender;
    private SmartCommand command;
    private Command bukkitCommand;

    public CommandIssuer(CommandSender sender, SmartCommand command, Command bukkitCommand) {
        this.sender = sender;
        this.command = command;
        this.bukkitCommand = bukkitCommand;
    }

    public boolean isPlayer() { return this.sender instanceof Player; }

    public Player getPlayer() { return isPlayer() ? (Player) this.sender : null; }

    public IssuerType getIssuerType() {
        return isPlayer() ? IssuerType.PLAYER : IssuerType.CONSOLE;
    }

    public CommandSender getCommandSender() { return this.sender; }

    public SmartCommand getCommand() { return this.command; }

    public Command getBukkitCommand() { return this.bukkitCommand; }

    public void sendCommandMessage(String path) {

        CommandContents attributes = this.command.getCommandContents();

        if(!attributes.hasCommandMessage())
            throw new NullPointerException(String.format("Command '%s' doesn't have CommandMessage.", this.command.getName()));

        String message = attributes.getCommandMessage().getString(path);

        this.sender.sendMessage(Utils.parseColors(message));
    }

    public void sendMessage(String message) {
        this.sender.sendMessage(Utils.parseColors(message));
    }

    public void sendHelp(String label, String[] args, int page, boolean usePagination) {

        CommandContents attributes = this.command.getCommandContents();

        if(attributes.hasCommandUsage()) {

            CommandUsage commandUsage = attributes.getCommandUsage();

            List<Usage> allUsages = commandUsage.findUsages(args);

            if(sender instanceof Player) sendPlayerHelp(allUsages, label, page, usePagination);
            else sendConsoleHelp(allUsages, label, page);
        }
    }

    private void sendConsoleHelp(List<Usage> usages, String label, int page) {

        List<String> consoleMessages = new ArrayList<>();

        usages.stream()
                .filter(usage -> usage.getConsoleMessage() != null)
                .forEach(usage -> consoleMessages.add(usage.getConsoleMessage().replace("%command%", label)));

        CommandContents attributes = this.command.getCommandContents();

        if(attributes.hasCommandHelp()) {

            CommandHelp commandHelp = attributes.getCommandHelp();

            Pagination pagination = new Pagination(commandHelp.getCommandsPerPage(), consoleMessages.size());

            if(page < pagination.getFirstPage()) page = pagination.getFirstPage();
            else if(page > pagination.getLastPage()) page = pagination.getLastPage();

            int index = pagination.getStartingIndex(page);

            for(int i = index; i < consoleMessages.size() && i < index + commandHelp.getCommandsPerPage(); i++)
                this.sender.sendMessage(consoleMessages.get(i));

        } else consoleMessages.forEach(sender::sendMessage);
    }

    private void sendPlayerHelp(List<Usage> usages, String label, int page, boolean usePagination) {

        Player player = getPlayer();

        List<TextComponent> components = getComponents(player, usages, label);

        CommandContents attributes = this.command.getCommandContents();

        CommandHelp commandHelp = attributes.getCommandHelp();

        Pagination pagination = new Pagination(commandHelp.getCommandsPerPage(), components.size());

        if(page < pagination.getFirstPage()) page = pagination.getFirstPage();
        else if(page > pagination.getLastPage()) page = pagination.getLastPage();

        boolean higherThanMinOfCommands = usePagination || pagination.countElementsAt(page) >= commandHelp.getMinOfCommands();

        if(!attributes.hasCommandHelp() || !higherThanMinOfCommands) {
            components.forEach(component -> getPlayer().spigot().sendMessage(component));
            return;
        }

        for(String line : commandHelp.getFormat()) {

            if(line.equals("%usages%")) {

                sendComponents(player, components, pagination, page);

            } else if(line.equals("%pagination%")) {

                TextComponent component = commandHelp.buildPaginationSystem(this.command, pagination, page);
                getPlayer().spigot().sendMessage(component);

            } else getPlayer().sendMessage(Utils.parseColors(line));
        }
    }

    private void sendComponents(Player player, List<TextComponent> components, Pagination pagination, int page) {

        int elementsPerPage = pagination.getElementsPerPage();
        int index = pagination.getStartingIndex(page);

        for(int i = index, count = 0; i < components.size() && count < elementsPerPage; i++, count++)
            player.spigot().sendMessage(components.get(i));
    }

    private List<TextComponent> getComponents(Player player, List<Usage> usages, String label) {

        List<TextComponent> components = new ArrayList<>();

        for(Usage usage : usages) {

            String permission = usage.getPermission();

            if(permission == null || player.hasPermission(permission)) {

                TextComponent component = usage.getUsageAsTextComponent(label);

                if(component != null) components.add(component);
            }
        }
        return components;
    }
}
