package fr.syrows.smartcommands.contents.usages;

import com.google.gson.JsonObject;
import fr.syrows.smartcommands.tools.EasyComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Usage {

    private TextComponent component;
    private String console, permission;

    public TextComponent getTextComponent() {
        return this.component;
    }

    public boolean hasComponent() {
        return this.component != null;
    }

    public String getConsoleMessage() {
        return this.console;
    }

    public boolean hasConsoleMessage() {
        return this.console != null;
    }

    public String getPermission() {
        return this.permission;
    }

    public boolean hasPermission() {
        return this.permission != null;
    }

    public static boolean isUsage(JsonObject object) {
        return object.has("type") && object.get("type").getAsString().equals("Usage.class");
    }

    public static Usage deserialize(JsonObject object) {

        Usage usage = new Usage();

        if(object.has("permission"))
            usage.permission = object.get("permission").getAsString();

        if(object.has("console"))
            usage.console = object.get("console").getAsString();

        if(object.has("component")) {
            EasyComponent component = EasyComponent.deserialize(object.get("component").getAsJsonObject());
            usage.component = component.getAsTextComponent();
        }
        return usage;
    }
}
