package fr.syrows.smartcommands.commands.issuers;

import fr.syrows.smartcommands.commands.CommandContents;
import fr.syrows.smartcommands.SmartCommand;
import fr.syrows.smartcommands.contents.CommandHelp;
import fr.syrows.smartcommands.contents.CommandUsage;
import fr.syrows.smartcommands.contents.usages.Usage;
import fr.syrows.smartcommands.tools.HelpSender;
import fr.syrows.smartcommands.tools.Pagination;
import fr.syrows.smartcommands.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

    public void sendTextComponent(String path) {

        CommandContents contents = this.command.getCommandContents();

        if(!isPlayer())  //Change error type
            throw new IllegalStateException("CommandSender must be a Player to send him a TextComponent.");

        if(!contents.hasCommandMessage())
            throw new NullPointerException(String.format("Command '%s' doesn't have CommandMessage.", this.command.getName()));

        TextComponent component = contents.getCommandMessage().getTextComponent(path);

        getPlayer().spigot().sendMessage(component);
    }

    public void sendCommandMessage(String path) {

        CommandContents contents = this.command.getCommandContents();

        if(!contents.hasCommandMessage())
            throw new NullPointerException(String.format("Command '%s' doesn't have CommandMessage.", this.command.getName()));

        String message = contents.getCommandMessage().getString(path);

        this.sender.sendMessage(Utils.parseColors(message));
    }

    public void sendMessage(String message) {
        this.sender.sendMessage(Utils.parseColors(message));
    }

    public void sendHelp(String label, String[] args, int page, boolean withPagination) {

        CommandContents contents = this.command.getCommandContents();

        if(contents.hasCommandUsage()) {

            CommandUsage commandUsage = contents.getCommandUsage();
            CommandHelp commandHelp = contents.getCommandHelp();

            List<Usage> usages = commandUsage.findUsages(this.sender, args);

            if(usages.size() == 0) return;

            Pagination pagination = null;

            if(contents.hasCommandHelp())
                pagination = new Pagination(page, commandHelp.getCommandsPerPage(), usages.size());

            HelpSender helpSender = new HelpSender(this.command, this, pagination);

            if(isPlayer()) helpSender.sendPlayerHelp(usages, label, withPagination);
            else helpSender.sendConsoleHelp(usages, label);
        }
    }
}
