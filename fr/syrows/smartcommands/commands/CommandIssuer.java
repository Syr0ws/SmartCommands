package fr.syrows.smartcommands.commands;

import fr.syrows.smartcommands.contents.CommandHelp;
import fr.syrows.smartcommands.contents.CommandUsage;
import fr.syrows.smartcommands.contents.usage.Usage;
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

    public CommandSender getSender() { return this.sender; }

    public SmartCommand getCommand() { return this.command; }

    public Command getBukkitCommand() { return this.bukkitCommand; }

    public void sendCommandMessage(String path) {
        this.sender.sendMessage(Utils.parseColors(this.command.getCommandMessage().getString(path)));
    }

    public void sendMessage(String message) {
        this.sender.sendMessage(Utils.parseColors(message));
    }

    public void sendHelp(String label, String[] args, int page, boolean forcePagination) {

        if(!this.command.useUsages()) return;

        if(page <= 0) page = 1;

        CommandUsage commandUsage = this.command.getCommandUsage();

        List<Usage> allUsages = commandUsage.findUsages(args);

        if(!(this.sender instanceof Player)) sendConsoleHelp(allUsages, label, page);
        else sendPlayerHelp(allUsages, label, page, forcePagination);
    }

    private void sendConsoleHelp(List<Usage> usages, String label, int page) {

        List<String> consoleMessages = new ArrayList<>();

        usages.stream()
                .filter(usage -> usage.getConsoleMessage() != null)
                .forEach(usage -> consoleMessages.add(usage.getConsoleMessage().replace("%command%", label)));

        if(!this.command.useHelp()) {
            consoleMessages.forEach(sender::sendMessage);
            return;
        }
        CommandHelp commandHelp = this.command.getCommandHelp();

        int index = page == 1 ? 0 : commandHelp.getCommandsPerPage() * (page - 1);

        if(index >= usages.size()) index = 0;

        for(int i = index; i < index + commandHelp.getCommandsPerPage(); i++) {

            this.sender.sendMessage(consoleMessages.get(i));

            if(i + 1 == consoleMessages.size()) break;
        }
    }

    private void sendPlayerHelp(List<Usage> usages, String label, int page, boolean forcePagination) {

        List<TextComponent> components = new ArrayList<>();

        for(Usage usage : usages) {

            String permission = usage.getPermission();

            if(permission != null && !getPlayer().hasPermission(permission)) continue;

            TextComponent component = usage.getUsageAsTextComponent(label);

            if(component != null) components.add(component);
        }

        if(!this.command.useHelp()) {
            components.forEach(component -> getPlayer().spigot().sendMessage(component));
            return;
        }
        CommandHelp commandHelp = this.command.getCommandHelp();

        int index = page == 1 ? 0 : commandHelp.getCommandsPerPage() * (page - 1);

        if(index >= usages.size()) {
            index = 0;
            page = 1;
        }

        boolean higherThanMinOfCommands = forcePagination || usages.size() - index >= commandHelp.getMinOfCommands();

        String top = commandHelp.getTop(), bottom = commandHelp.getBottom();

        if(higherThanMinOfCommands && top != null) getPlayer().sendMessage(Utils.parseColors(top));

        for(int i = index; i < index + commandHelp.getCommandsPerPage(); i++) {

            if(i >= components.size()) break;

            getPlayer().spigot().sendMessage(components.get(i));

            if(i + 1 == components.size()) break;
        }

        if(higherThanMinOfCommands) {

            getPlayer().sendMessage(" ");

            if(bottom != null) getPlayer().spigot().sendMessage(commandHelp.buildPagination(this.command, components, page));

            getPlayer().sendMessage(Utils.parseColors(commandHelp.getBottom()));
        }
    }

    public enum IssuerType {

        PLAYER, CONSOLE;
    }
}
