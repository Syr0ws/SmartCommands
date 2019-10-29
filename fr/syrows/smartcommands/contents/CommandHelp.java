package fr.syrows.smartcommands.contents;

import fr.syrows.smartcommands.SmartCommand;
import fr.syrows.smartcommands.tools.EasyComponent;
import fr.syrows.smartcommands.tools.Pagination;
import fr.syrows.smartcommands.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;

public class CommandHelp {

    private String[] previousPage, nextPage, pagination, format;
    private int minOfCommands, commandsPerPage;

    public String[] getFormat() { return format; }

    public String[] getPreviousPage() { return previousPage; }

    public String[] getNextPage() { return nextPage; }

    public int getMinOfCommands() { return minOfCommands; }

    public int getCommandsPerPage() { return commandsPerPage; }

    public TextComponent buildPaginationSystem(SmartCommand command, Pagination pagination, int page) {

        EasyComponent base = new EasyComponent().setText("");

        for(String argument : this.pagination) {

            String text, showText;

            switch (argument) {
                default:
                    text = argument.equals("%page%") ? String.valueOf(page) : Utils.parseColors(argument);
                    base.addExtra(new EasyComponent().setText(text));
                    break;
                case "%previous_page%":

                    EasyComponent previousPage = new EasyComponent();

                    text = Utils.parseColors(page != 1 ? this.previousPage[0] : this.previousPage[2]);
                    showText = Utils.parseColors(page != 1 ? this.previousPage[1] : this.previousPage[3]);

                    previousPage.setText(text);

                    if(!showText.equals("")) previousPage.showText(showText);

                    if(!pagination.isFirstPage(page)) previousPage.runCommand(String.format("%s help %d", command.getName(), page - 1));

                    base.addExtra(previousPage);
                    break;
                case "%next_page%":

                    EasyComponent nextPage = new EasyComponent();

                    boolean isLast = pagination.isLastPage(page);

                    text = Utils.parseColors(!isLast ? this.nextPage[0] : this.nextPage[2]);
                    showText = Utils.parseColors(!isLast ? this.nextPage[1] : this.nextPage[3]);

                    nextPage.setText(text);

                    if(!showText.equals("")) nextPage.showText(showText);

                    if(!isLast) nextPage.runCommand(String.format("%s help %d", command.getName(), page + 1));

                    base.addExtra(nextPage);
                    break;
            }
        }
        return base.build();
    }
}
