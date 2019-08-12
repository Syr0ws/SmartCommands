package fr.syrows.smartcommands.tools;

import fr.syrows.smartcommands.commands.SmartCommand;
import fr.syrows.smartcommands.commands.CommandExecutor;
import fr.syrows.smartcommands.commands.CommandIssuer;
import fr.syrows.smartcommands.contents.CommandUsage;
import fr.syrows.smartcommands.contents.usage.Usage;
import fr.syrows.smartcommands.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BukkitCommand extends Command {

    private SmartCommand command;

    public BukkitCommand(SmartCommand command) {
        super(command.getName(), command.getDescription(), command.getUsage(), command.getAliases());
        this.command = command;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {

        CommandIssuer issuer = new CommandIssuer(sender, this.command, this);

        List<CommandIssuer.IssuerType> issuers = this.command.getIssuers();

        if(!issuer.isPlayer() && !issuers.contains(CommandIssuer.IssuerType.CONSOLE)) {
            issuer.sendMessage("Command can be executed only by player.");
            return true;
        }

        if(issuer.isPlayer() && !issuers.contains(CommandIssuer.IssuerType.PLAYER)) {
            issuer.sendMessage("Command can be executed only by console.");
            return true;
        }

        CommandExecutor executor = this.command.getExecutor();

        boolean executed = executor.onCommand(this.command, issuer, label, args);

        if(!executed && (this.command.useUsages() || this.command.useHelp())) {

            if(args.length == 0) args = new String[]{""};

            if(args.length == 2 && args[0].equalsIgnoreCase("help") && Utils.isInt(args[1])) {
                issuer.sendHelp(sender, this.command, label, args, Integer.parseInt(args[1]));
                return true;
            }
            issuer.sendHelp(sender, this.command, label, args, 1);
        }
        return executed;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) throws IllegalArgumentException {

        if(!this.command.useTabComplete()) return super.tabComplete(sender, label, args);

        CommandUsage commandUsage = this.command.getCommandUsage();

        if(commandUsage == null)
            throw new NullPointerException(String.format("Cannot find a CommandUsage for the command %s", this.command.getName()));

        Map<String, List<Usage>> allTabCompletes = commandUsage.getTabCompletes(args);

        List<String> tabCompletes = new ArrayList<>();

        for(Map.Entry<String, List<Usage>> entry : allTabCompletes.entrySet()) {

            String key = entry.getKey();
            List<Usage> usages = entry.getValue();

            for(Usage usage : usages) {

                String permission = usage.getPermission();

                if(permission == null || sender.hasPermission(permission)) {
                    tabCompletes.add(key);
                    break;
                }
            }
        }
        List<String> newTabCompletes = this.command.getExecutor().onTabComplete(this.command, sender, label, args, tabCompletes);

        return newTabCompletes != null ? newTabCompletes : tabCompletes;
    }
}
