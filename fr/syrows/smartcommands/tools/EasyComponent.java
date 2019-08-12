package fr.syrows.smartcommands.tools;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class EasyComponent {

    private TextComponent tc;

    public EasyComponent() {
        this.tc = new TextComponent();
    }

    public EasyComponent(EasyComponent component) {
        this.tc = new TextComponent(component.build());
    }

    public EasyComponent setText(String text) {
        tc.setText(text);
        return this;
    }

    public EasyComponent showText(String text) {
        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(text).create()));
        return this;
    }

    public EasyComponent setColor(ChatColor color) {
        if(color != null) tc.setColor(color);
        return this;
    }

    public EasyComponent suggestCommand(String command) {
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + command));
        return this;
    }

    public EasyComponent runCommand(String command) {
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command));
        return this;
    }

    public EasyComponent addExtra(TextComponent tc) {
        tc.addExtra(tc);
        return this;
    }

    public EasyComponent addExtra(EasyComponent builder) {
        tc.addExtra(builder.build());
        return this;
    }

    public TextComponent build() {
        return tc;
    }
}
