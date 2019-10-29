package fr.syrows.smartcommands.contents.usages;

import fr.syrows.smartcommands.tools.EasyComponent;
import fr.syrows.smartcommands.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Usage {

    private String text, showText, permission, console, command;
    private ChatColor color;
    private UsageAction action;

    public String getText() { return text; }

    public String getShowText() { return showText; }

    public String getPermission() { return permission; }

    public String getConsoleMessage() { return console; }

    public String getCommand() { return command; }

    public ChatColor getColor() { return color; }

    public UsageAction getAction() { return action; }

    public TextComponent getUsageAsTextComponent(String label) {

        if(this.text == null) return null;

        EasyComponent component = new EasyComponent()
                .setText(Utils.parseColors(this.text.replace("%command%", label)));

        if(this.showText != null) component
                .showText(Utils.parseColors(this.showText.replace("%command%", label)));

        if(this.color != null) component.setColor(this.color);

        if(this.action != null) {

            switch (this.action) {
                case RUN_COMMAND:
                    component.runCommand(this.command.replace("%command%", label));
                    break;
                case SUGGEST_COMMAND:
                    component.suggestCommand(this.command.replace("%command%", label));
                    break;
            }
        }
        return component.build();
    }
}
