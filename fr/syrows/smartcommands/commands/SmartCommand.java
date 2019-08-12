package fr.syrows.smartcommands.commands;

import fr.syrows.smartcommands.contents.CommandHelp;
import fr.syrows.smartcommands.contents.CommandMessage;
import fr.syrows.smartcommands.contents.CommandUsage;

import java.util.ArrayList;
import java.util.List;

public class SmartCommand {

    private String name, description, usage;
    private List<String> aliases;
    private List<CommandIssuer.IssuerType> issuers;
    private boolean usages, help, tabComplete;

    private CommandHelp commandHelp;
    private CommandMessage commandMessage;
    private CommandUsage commandUsage;

    private CommandExecutor executor;

    public SmartCommand() {
        this.description = "";
        this.usage = "";
        this.aliases = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    void setName(String name) { this.name = name; }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public List<CommandIssuer.IssuerType> getIssuers() { return issuers; }

    public boolean useUsages() { return usages; }

    public boolean useHelp() {
        return help;
    }

    public boolean useTabComplete() {
        return tabComplete;
    }

    public CommandHelp getCommandHelp() { return commandHelp; }

    void setCommandHelp(CommandHelp commandHelp) { this.commandHelp = commandHelp; }

    public CommandMessage getCommandMessage() { return commandMessage; }

    void setCommandMessage(CommandMessage commandMessage) { this.commandMessage = commandMessage; }

    public CommandUsage getCommandUsage() { return commandUsage; }

    void setCommandUsage(CommandUsage commandUsage) { this.commandUsage = commandUsage; }

    public CommandExecutor getExecutor() { return this.executor; }

    void setExecutor(CommandExecutor executor) { this.executor = executor; }
}
