package fr.syrows.smartcommands.tools;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ClickEvent;


public class EasyComponent {

    private TextComponent component;

    public EasyComponent() {
        this.component = new TextComponent("");
    }

    public EasyComponent(TextComponent component) {
        this.component = component;
    }

    public EasyComponent setText(String text) {
        this.component.setText(parseColors(text));
        return this;
    }

    public EasyComponent setColor(ChatColor color) {
        this.component.setColor(color);
        return this;
    }

    public EasyComponent setBold(boolean bold) {
        this.component.setBold(bold);
        return this;
    }

    public EasyComponent setItalic(boolean italic) {
        this.component.setItalic(italic);
        return this;
    }

    public EasyComponent setUnderlined(boolean underlined) {
        this.component.setUnderlined(underlined);
        return this;
    }

    public EasyComponent setObfuscated(boolean obfuscated) {
        this.component.setObfuscated(obfuscated);
        return this;
    }

    public EasyComponent setStrikethrough(boolean strikethrough) {
        this.component.setStrikethrough(strikethrough);
        return this;
    }

    public EasyComponent showText(String text) {
        BaseComponent[] base = new ComponentBuilder(parseColors(text)).create();
        this.component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, base));
        return this;
    }

    public EasyComponent runCommand(String command) {
        this.component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command));
        return this;
    }

    public EasyComponent suggestCommand(String command) {
        this.component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + command));
        return this;
    }

    public EasyComponent openUrl(String url) {
        this.component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        return this;
    }

    public EasyComponent addExtra(EasyComponent component) {
        this.component.addExtra(component.getAsTextComponent());
        return this;
    }

    public EasyComponent addExtra(TextComponent component) {
        this.component.addExtra(component);
        return this;
    }

    public TextComponent getAsTextComponent() {
        return this.component;
    }

    private String parseColors(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static EasyComponent deserialize(JsonObject object) {

        EasyComponent component = new EasyComponent();

        if(object.has("text"))
            component.setText(object.get("text").getAsString());

        if(object.has("showText"))
            component.showText(object.get("showText").getAsString());

        if(object.has("color"))
            component.setColor(ChatColor.valueOf(object.get("color").getAsString()));

        if(object.has("suggestCommand"))
            component.suggestCommand(object.get("suggestCommand").getAsString());

        if(object.has("runCommand"))
            component.runCommand(object.get("runCommand").getAsString());

        if(object.has("openUrl"))
            component.openUrl(object.get("openUrl").getAsString());

        component.setBold(object.has("bold") && object.get("bold").getAsBoolean());
        component.setItalic(object.has("italic") && object.get("italic").getAsBoolean());
        component.setObfuscated(object.has("obfuscated") && object.get("obfuscated").getAsBoolean());
        component.setStrikethrough(object.has("strikethrough") && object.get("strikethrough").getAsBoolean());

        if(object.has("extra")) {

            JsonObject extra = object.get("extra").getAsJsonObject();
            component.addExtra(EasyComponent.deserialize(extra));
        }
        return component;
    }
}
