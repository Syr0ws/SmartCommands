package fr.syrows.smartcommands.tools;

import fr.syrows.smartcommands.commands.CommandContents;
import fr.syrows.smartcommands.commands.CommandExecutor;
import fr.syrows.smartcommands.SmartCommand;
import fr.syrows.smartcommands.commands.issuers.CommandIssuer;
import fr.syrows.smartcommands.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class BukkitCommand extends Command {

    private SmartCommand command;

    public BukkitCommand(SmartCommand command) {
        super(command.getName(), command.getDescription(), command.getUsage(), command.getAliases());
        this.command = command;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {

        CommandIssuer issuer = new CommandIssuer(sender, this.command, this);

        if(!this.command.canExecute(issuer.getIssuerType())) return false;

        CommandExecutor executor = this.command.getExecutor();
        CommandContents attributes = this.command.getCommandContents();

        boolean executed = executor.onCommand(this.command, issuer, label, args);

        if(attributes.hasCommandUsage()) {

            if(args.length == 2 && args[0].equalsIgnoreCase("help") && Utils.isInt(args[1])) {

                issuer.sendHelp(label, args, Integer.parseInt(args[1]), true);
                executed = true;

            } else if(!executed) issuer.sendHelp(label, args.length == 0 ? new String[]{""} : args, 1, false);
        }
        return executed;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) throws IllegalArgumentException {

        CommandExecutor executor = this.command.getExecutor();

        List<String> completions = new ArrayList<>();

        CommandIssuer issuer = new CommandIssuer(sender, this.command, this);
        CommandContents contents = this.command.getCommandContents();

        boolean canTabComplete = this.command.canExecute(issuer.getIssuerType())
                && command.useTabCompleter() && contents.hasCommandUsage();

        if(canTabComplete) completions = contents.getCommandUsage().findCompletions(sender, this.command, args);

        List<String> customCompletions = executor.onTabComplete(command, sender, label, args, completions);

        return customCompletions == null ? completions : customCompletions;
    }
}
