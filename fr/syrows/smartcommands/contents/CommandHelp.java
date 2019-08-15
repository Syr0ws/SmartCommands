package fr.syrows.smartcommands.contents;

import fr.syrows.smartcommands.commands.SmartCommand;

import fr.syrows.smartcommands.tools.EasyComponent;
import fr.syrows.smartcommands.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;

public class CommandHelp {

    private String top, bottom;
    private String[] previousPage, nextPage, format;
    private int minOfCommands, commandsPerPage;

    public TextComponent buildPagination(SmartCommand command, List<TextComponent> components, int page) {

        int index = page == 1 ? 0 : this.commandsPerPage * (page - 1);

        EasyComponent base = new EasyComponent().setText("");

        for(String argument : this.format) {

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

                    if(page != 1) previousPage.runCommand(String.format("%s help %d", command.getName(), page - 1));

                    base.addExtra(previousPage);
                    break;
                case "%next_page%":

                    EasyComponent nextPage = new EasyComponent();

                    boolean isLast = index + this.commandsPerPage >= components.size();

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

    public String getTop() { return top; }

    public String getBottom() { return bottom; }

    public String[] getFormat() { return format; }

    public String[] getPreviousPage() { return previousPage; }

    public String[] getNextPage() { return nextPage; }

    public int getMinOfCommands() { return minOfCommands; }

    public int getCommandsPerPage() { return commandsPerPage; }
}
