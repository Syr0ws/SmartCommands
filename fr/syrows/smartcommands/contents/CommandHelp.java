package fr.syrows.smartcommands.contents;

public class CommandHelp {

    private String[] previousPage, nextPage, pagination, format;
    private int minOfCommands, commandsPerPage;

    public String[] getFormat() { return this.format; }

    public String[] getPreviousPage() { return this.previousPage; }

    public String[] getNextPage() { return this.nextPage; }

    public String[] getPagination() { return this.pagination; }

    public int getMinOfCommands() { return this.minOfCommands; }

    public int getCommandsPerPage() { return this.commandsPerPage; }
}
