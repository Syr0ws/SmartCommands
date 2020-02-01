package fr.syrows.smartcommands;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import fr.syrows.smartcommands.commands.CommandContents;
import fr.syrows.smartcommands.commands.CommandExecutor;
import fr.syrows.smartcommands.commands.issuers.IssuerType;

import java.lang.reflect.Type;
import java.util.*;

public class SmartCommand {

    private String name, description, usage, resource;
    private List<String> aliases;
    private List<IssuerType> issuers;
    private boolean tabComplete, resourceOnly;

    private Map<String, List<String>> completions = new HashMap<>();

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

    public String getResourcePath() {
        return this.resource;
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

    public boolean isResourceOnly() {
        return this.resourceOnly;
    }

    public boolean hasResourcePath() {
        return this.resource != null;
    }

    public CommandContents getCommandContents() {
        return this.contents;
    }

    void setCommandContents(CommandContents contents) {
        this.contents = contents;
    }

    public CommandExecutor getExecutor() {
        return this.executor;
    }

    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }

    public boolean canExecute(IssuerType type) {
        return this.issuers.contains(type);
    }

    public List<String> getCompletions(String key) {
        return this.completions.getOrDefault(key, Collections.emptyList());
    }

    public void registerCompletions(String key, List<String> completions) {
        this.completions.put(key, completions);
    }

    public void unregisterCompletions(String key) {
        this.completions.remove(key);
    }

    public static SmartCommand deserialize(String name, JsonObject object) {

        SmartCommand command = new SmartCommand(name);

        command.description = object.has("description") ?
                object.get("description").getAsString() : "No description";

        command.usage = object.has("usage") ?
                object.get("usage").getAsString() : "/" + command;

        command.tabComplete = object.has("tabComplete")
                && object.get("tabComplete").getAsBoolean();

        if(object.has("resource")) {

            command.resource = object.get("resource").getAsString();

            command.resourceOnly = object.has("resourceOnly")
                    && object.get("resourceOnly").getAsBoolean();
        }

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
