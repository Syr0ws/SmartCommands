package fr.syrows.smartcommands.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface CommandExecutor {

    boolean onCommand(SmartCommand cmd, CommandIssuer issuer, String label, String[] args);

    List<String> onTabComplete(SmartCommand command, CommandSender sender, String label, String[] args, List<String> found);
}
