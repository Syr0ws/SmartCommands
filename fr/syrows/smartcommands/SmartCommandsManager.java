package fr.syrows.smartcommands;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import fr.syrows.smartcommands.commands.CommandContents;
import fr.syrows.smartcommands.contents.CommandHelp;
import fr.syrows.smartcommands.contents.CommandMessage;
import fr.syrows.smartcommands.contents.CommandUsage;
import fr.syrows.smartcommands.utils.FileUtils;
import org.bukkit.plugin.Plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;

public class SmartCommandsManager {

    private SmartCommandsAPI api;

    private Map<String, SmartCommand> smartCommands = new HashMap<>();

    public SmartCommandsManager(SmartCommandsAPI api) {
        this.api = api;
    }

    public void reloadCommandContents() {

        if(!this.api.useContentsFile()) return;

        this.api.getLogger().log(Level.INFO, "Reloading command contents...");

        Map<String, JsonObject> contents = this.loadCommandContents();
        Map<String, CommandContents> commandAttributes = this.parseCommandContents(contents);

        this.smartCommands.forEach((name, command) -> {

            CommandContents attributes;

            if(commandAttributes.containsKey(name)) attributes = commandAttributes.get(name);
            else attributes = new CommandContents(null, null, null);

            command.setCommandContents(attributes);
        });
        this.api.getLogger().log(Level.INFO, "Command contents reloaded.");
    }

    public List<SmartCommand> getSmartCommands() {
        return new ArrayList<>(this.smartCommands.values());
    }

    public SmartCommand getSmartCommand(String name) {
        return this.smartCommands.getOrDefault(name, null);
    }

    public boolean exists(String name) {
        return this.smartCommands.containsKey(name);
    }

    private Map<String, JsonObject> loadCommands() {

        this.api.getLogger().log(Level.INFO, "Loading smartCommands...");

        String commandResourcePath = this.api.getCommandResourcePath();

        InputStream stream = this.api.getPlugin().getResource(commandResourcePath);

        if(stream == null)
            throw new NullPointerException(String.format("Cannot find the resource 'smartCommands.json' at '%s'.", commandResourcePath));

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        Type type = new TypeToken<Map<String, JsonObject>>(){}.getType();

        Map<String, JsonObject> commands = SmartCommandsAPI.gson.fromJson(reader, type);

        this.api.getLogger().log(Level.INFO, String.format("%d command%s loaded.", commands.size(), commands.size() <= 1 ? "" : "s"));

        try { reader.close();
        } catch (IOException e) { e.printStackTrace(); }

        return commands;
    }

    private Map<String, JsonObject> loadCommandContents() {

        Map<String, JsonObject> commandContents = new HashMap<>();

        if(!this.api.useContentsFile()) return commandContents;

        Plugin plugin = this.api.getPlugin();

        Path path = Paths.get(plugin.getDataFolder() + "/commands_contents.json");

        if(!Files.exists(path) && this.api.canCreateContentsFile()) {
            FileUtils.createDirectory(this.api, plugin.getDataFolder().toPath());
            FileUtils.createFileFromResource(this.api, path, this.api.getContentsResourcePath(), false);
        }

        BufferedReader reader = null;

        try {

            if(!this.api.canCreateContentsFile()) {

                String contentsResourcePath = this.api.getContentsResourcePath();

                InputStream stream = plugin.getResource(contentsResourcePath);

                if(stream == null)
                    throw new NullPointerException(String.format("Cannot find the resource 'commands_contents.json' at '%s'.", contentsResourcePath));

                reader = new BufferedReader(new InputStreamReader(stream));

            } else reader = Files.newBufferedReader(path);

            this.api.getLogger().log(Level.INFO, "Command contents loaded.");

        } catch (IOException e) {

            this.api.getLogger().log(Level.SEVERE, "Cannot load command contents.");

            e.printStackTrace();
        }

        if(reader == null) return commandContents;

        Type type = new TypeToken<Map<String, JsonObject>>(){}.getType();
        commandContents = SmartCommandsAPI.gson.fromJson(reader, type);

        return commandContents;

    }

    private Map<String, CommandContents> parseCommandContents(Map<String, JsonObject> loadedContents) {

        Map<String, CommandContents> parsedContents = new HashMap<>();

        loadedContents.forEach((name, object) -> {

            CommandHelp commandHelp = null;
            CommandUsage commandUsage = null;
            CommandMessage commandMessage = null;

            if(object.has("commandHelp"))
                commandHelp = SmartCommandsAPI.gson.fromJson(object.get("commandHelp"), CommandHelp.class);

            if(object.has("commandUsage"))
                commandUsage = new CommandUsage(object.get("commandUsage").getAsJsonObject());

            if(object.has("commandMessage"))
                commandMessage = new CommandMessage(object.get("commandMessage").getAsJsonObject());

            CommandContents contents = new CommandContents(commandHelp, commandUsage, commandMessage);

            parsedContents.put(name.toLowerCase(), contents);
        });
        return parsedContents;
    }

    void loadSmartCommands() {

        Map<String, JsonObject> commands = this.loadCommands();
        Map<String, JsonObject> contents = this.loadCommandContents();

        Map<String, CommandContents> commandAttributes = this.parseCommandContents(contents);

        for (Map.Entry<String, JsonObject> entry : commands.entrySet()) {

            String name = entry.getKey().toLowerCase();
            JsonObject object = entry.getValue();

            CommandContents attributes;

            if (this.api.useContentsFile() && commandAttributes.containsKey(name)) attributes = commandAttributes.get(name);
            else attributes = new CommandContents(null, null, null);

            SmartCommand command = SmartCommand.deserialize(name, object);
            command.setCommandContents(attributes);

            this.smartCommands.put(name, command);
        }
    }
}
