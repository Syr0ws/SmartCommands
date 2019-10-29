package fr.syrows.smartcommands;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import fr.syrows.smartcommands.commands.CommandContents;
import fr.syrows.smartcommands.commands.CommandExecutor;
import fr.syrows.smartcommands.commands.issuers.IssuerType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SmartCommand {

    private String name, description, usage;
    private List<String> aliases;
    private List<IssuerType> issuers;
    private boolean tabComplete;

    private CommandContents contents;
    private CommandExecutor executor;

    public SmartCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public String getUsage() {
        return this.usage;
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    public List<IssuerType> getIssuers() {
        return this.issuers;
    }

    public boolean useTabCompleter() {
        return this.tabComplete;
    }

    public CommandContents getCommandContents() {
        return this.contents;
    }

    void setCommandContents(CommandContents contents) { this.contents = contents; }

    public CommandExecutor getExecutor() {
        return this.executor;
    }

    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }

    public boolean canExecute(IssuerType type) {
        return this.issuers.contains(type);
    }

    public static SmartCommand deserialize(String name, JsonObject object) {

        SmartCommand command = new SmartCommand(name);

        command.description = object.has("description") ?
                object.get("description").getAsString() : "No description";

        command.usage = object.has("usage") ?
                object.get("usage").getAsString() : "/" + command;

        command.tabComplete = object.has("tabComplete")
                && object.get("tabComplete").getAsBoolean();

        if(object.has("aliases")) {

            Type type = new TypeToken<List<String>>(){}.getType();
            command.aliases = SmartCommandsAPI.gson.fromJson(object.get("aliases"), type);

        } else command.aliases = new ArrayList<>();

        if(object.has("issuers")) {

            Type type = new TypeToken<List<IssuerType>>(){}.getType();
            command.issuers = SmartCommandsAPI.gson.fromJson(object.get("issuers"), type);

        } else throw new NullPointerException(String.format("Command '%s' doesn't have issuer(s).", name));

        return command;
    }
}
