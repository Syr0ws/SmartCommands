package fr.syrows.smartcommands.tools;

import fr.syrows.smartcommands.SmartCommand;
import fr.syrows.smartcommands.commands.CommandContents;
import fr.syrows.smartcommands.commands.issuers.CommandIssuer;
import fr.syrows.smartcommands.contents.CommandHelp;
import fr.syrows.smartcommands.contents.usages.Usage;
import fr.syrows.smartcommands.utils.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HelpSender {

    private SmartCommand command;
    private CommandIssuer issuer;
    private Pagination pagination;

    public HelpSender(SmartCommand command, CommandIssuer issuer, Pagination pagination) {
        this.command = command;
        this.issuer = issuer;
        this.pagination = pagination;
    }

    public void sendConsoleHelp(List<Usage> usages, String label) {

        CommandContents contents = this.command.getCommandContents();

        if(contents.hasCommandHelp()) {

            int elementsPerPage = this.pagination.getElementsPerPage();
            int index = this.pagination.getStartingIndex();

            for(int i = index, count = 0; i < usages.size() && count < elementsPerPage; i++, count++)
                sendUsageAsMessage(this.issuer.getCommandSender(), usages.get(i), label);

        } else sendConsoleHelp(usages, label);
    }

    public void sendPlayerHelp(List<Usage> usages, String label, boolean withPagination) {

        Player player = this.issuer.getPlayer();

        CommandContents contents = this.command.getCommandContents();
        CommandHelp commandHelp = contents.getCommandHelp();

        if(withPagination || contents.hasCommandHelp() && this.pagination.countElements() >= commandHelp.getMinOfCommands()) {

            for(String line : commandHelp.getFormat()) {

                if(line.equals("%usages%")) {

                    sendComponents(player, usages, label);

                } else if(line.equals("%pagination%")) {

                    player.spigot().sendMessage(buildPaginationText());

                } else player.sendMessage(Utils.parseColors(line));
            }

        } else sendComponents(player, usages, label);
    }

    private void sendComponents(Player player, List<Usage> usages, String label) {

        if(this.pagination != null) {

            int elementsPerPage = this.pagination.getElementsPerPage();
            int index = this.pagination.getStartingIndex();

            for(int i = index, count = 0; i < usages.size() && count < elementsPerPage; i++, count++)
                sendUsageAsComponent(player, usages.get(i), label);

        } else usages.forEach(usage -> sendUsageAsComponent(player, usage, label));
    }

    private void sendUsageAsComponent(Player player, Usage usage, String label) {

        if(usage.hasComponent()) {

            TextComponent component = usage.getTextComponent();

            component.setText(component.getText().replace("%command%", label));

            player.spigot().sendMessage(component);
        }
    }

    private void sendUsageAsMessage(CommandSender sender, Usage usage, String label) {

        if(usage.hasConsoleMessage()) {

            String message = usage.getConsoleMessage();

            sender.sendMessage(message.replace("%command%", label));
        }
    }

    private TextComponent buildPaginationText() {

        CommandContents contents = this.command.getCommandContents();
        CommandHelp commandHelp = contents.getCommandHelp();

        EasyComponent base = new EasyComponent().setText("");

        int page = this.pagination.getCurrentPage();

        for (String argument : commandHelp.getPagination()) {

            String text, showText;

            switch (argument) {
                default:
                    text = argument.equals("%page%") ? String.valueOf(this.pagination.getCurrentPage()) : Utils.parseColors(argument);
                    base.addExtra(new EasyComponent().setText(text));
                    break;
                case "%previous_page%":

                    EasyComponent previousPage = new EasyComponent();

                    String[] previousPageFormat = commandHelp.getPreviousPage();

                    text = Utils.parseColors(page != 1 ? previousPageFormat[0] : previousPageFormat[2]);
                    showText = Utils.parseColors(page != 1 ? previousPageFormat[1] : previousPageFormat[3]);

                    previousPage.setText(text);

                    if (!showText.equals("")) previousPage.showText(showText);

                    if (!this.pagination.isFirstPage())
                        previousPage.runCommand(String.format("%s help %d", this.command.getName(), page - 1));

                    base.addExtra(previousPage);
                    break;
                case "%next_page%":

                    EasyComponent nextPage = new EasyComponent();

                    String[] nextPageFormat = commandHelp.getNextPage();

                    boolean isLast = this.pagination.isLastPage();

                    text = Utils.parseColors(!isLast ? nextPageFormat[0] : nextPageFormat[2]);
                    showText = Utils.parseColors(!isLast ? nextPageFormat[1] : nextPageFormat[3]);

                    nextPage.setText(text);

                    if (!showText.equals("")) nextPage.showText(showText);

                    if (!isLast) nextPage.runCommand(String.format("%s help %d", this.command.getName(), page + 1));

                    base.addExtra(nextPage);
                    break;
            }
        }
        return base.getAsTextComponent();
    }
}
