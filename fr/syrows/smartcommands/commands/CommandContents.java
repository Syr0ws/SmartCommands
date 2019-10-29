package fr.syrows.smartcommands.commands;

import fr.syrows.smartcommands.contents.CommandHelp;
import fr.syrows.smartcommands.contents.CommandMessage;
import fr.syrows.smartcommands.contents.CommandUsage;

public class CommandContents {

    private CommandHelp commandHelp;
    private CommandMessage commandMessage;
    private CommandUsage commandUsage;

    public CommandContents(CommandHelp cmdHelp, CommandUsage cmdUsage, CommandMessage cmdMessage) {
        this.commandHelp = cmdHelp;
        this.commandUsage = cmdUsage;
        this.commandMessage = cmdMessage;
    }

    public CommandHelp getCommandHelp() {
        return this.commandHelp;
    }

    public boolean hasCommandHelp() {
        return this.commandHelp != null;
    }

    public CommandMessage getCommandMessage() {
        return this.commandMessage;
    }

    public boolean hasCommandMessage() {
        return this.commandMessage != null;
    }

    public CommandUsage getCommandUsage() {
        return this.commandUsage;
    }

    public boolean hasCommandUsage() {
        return this.commandUsage != null;
    }
}
