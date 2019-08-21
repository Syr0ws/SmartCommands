package fr.syrows.smartcommands.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import fr.syrows.smartcommands.SmartCommandsAPI;
import fr.syrows.smartcommands.contents.CommandHelp;
import fr.syrows.smartcommands.contents.CommandMessage;
import fr.syrows.smartcommands.contents.CommandUsage;
import fr.syrows.smartcommands.tools.BukkitCommand;
import fr.syrows.smartcommands.utils.FileUtils;
import fr.syrows.smartcommands.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class CommandManager {

    private SmartCommandsAPI api;

    private CommandMap commandMap;
    private Map<String, SmartCommand> commands;

    public CommandManager(SmartCommandsAPI api) {
        this.api = api;
        this.commands = new HashMap<>();
    }

    public void registerCommand(String name, CommandExecutor executor) {

        Logger.log(Level.INFO, String.format("Registering %s command...", name));

        if(this.commandMap == null)
            throw new IllegalStateException("CommandMap cannot be null. Please, initialize the API before registering commands.");

        if(!commands.containsKey(name))
            throw new NullPointerException(String.format("Command %s is not registered.", name));

        if(executor == null)
            throw new IllegalArgumentException("CommandExecutor cannot be null");

        SmartCommand command = this.commands.get(name);
        command.setExecutor(executor);

        BukkitCommand bukkitCommand = new BukkitCommand(command);

        this.commandMap.register(name, bukkitCommand);

        Logger.log(Level.INFO, String.format("Command %s registered.", name));
    }

    public boolean exist(String name) {
        return this.commands.containsKey(name);
    }

    public SmartCommand getCommand(String name) {
        return this.commands.getOrDefault(name, null);
    }

    private void setupCommandMap() {

        Logger.log(Level.INFO, "Initializing CommandMap...");

        try {

            Field commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMap.setAccessible(true);

            this.commandMap = (CommandMap) commandMap.get(Bukkit.getServer());

            Logger.log(Level.INFO, "CommandMap initialized.");

        } catch (NoSuchFieldException | IllegalAccessException e) {

            Logger.log(Level.SEVERE, "Cannot initialize CommandMap.");

            e.printStackTrace();
        }
    }

    private void loadCommands() {

        Logger.log(Level.INFO, "Loading commands...");

        String commandResourcePath = this.api.getCommandResourcePath();

        InputStream stream = this.api.getPlugin().getResource(commandResourcePath);

        if(stream == null)
            throw new NullPointerException(String.format("Cannot find the resource command_contents.json at %s", commandResourcePath));

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        Type type = new TypeToken<Map<String, SmartCommand>>(){}.getType();

        Map<String, SmartCommand> commands = this.api.getGson().fromJson(reader, type);

        if(commands == null) return;

        commands.forEach((name, command) -> command.setName(name));

        this.commands = commands;

        Logger.log(Level.INFO, String.format("%d command%s loaded.", commands.size(), commands.size() <= 1 ? "" : "s"));

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCommandContents() {

        if(!this.api.useContentsFile()) return;

        Logger.log(Level.INFO, "Loading command contents...");

        Plugin plugin = this.api.getPlugin();

        Path path = Paths.get(plugin.getDataFolder() + File.separator + "commands_contents.json");

        if(!Files.exists(path) && this.api.canCreateContentsFile()) {
            FileUtils.createDirectory(plugin.getDataFolder().toPath());
            FileUtils.createFileFromResource(plugin, path, this.api.getContentsResourcePath(), false);
        }
        Gson gson = this.api.getGson();

        Map<String, JsonObject> commandContents = new HashMap<>();

        try {

            Logger.log(Level.INFO, "Loading data...");

            BufferedReader reader;

            if(!this.api.canCreateContentsFile()) {

                String contentsResourcePath = this.api.getContentsResourcePath();

                InputStream stream = plugin.getResource(contentsResourcePath);

                if(stream == null)
                    throw new NullPointerException(String.format("Cannot find the resource commands_contents.json at %s", contentsResourcePath));

                reader = new BufferedReader(new InputStreamReader(stream));

            } else {

                reader = new BufferedReader(new FileReader(path.toFile()));
            }
            Type type = new TypeToken<Map<String, JsonObject>>(){}.getType();
            Map<String, JsonObject> contents = gson.fromJson(reader, type);

            if(contents != null) commandContents = contents;

            Logger.log(Level.INFO, "Data loaded.");

            reader.close();

        } catch (IOException e) {

            Logger.log(Level.SEVERE, "Cannot load command contents.");

            e.printStackTrace();
        }
        Logger.log(Level.INFO, "Assigning data...");

        for(Map.Entry<String, JsonObject> entry : commandContents.entrySet()) {

            String name = entry.getKey();

            if(!exist(name)) continue;

            JsonObject object = entry.getValue();

            SmartCommand command = getCommand(name);

            if(object.has("commandHelp"))
                command.setCommandHelp(SmartCommandsAPI.getApi().getGson().fromJson(object.get("commandHelp"), CommandHelp.class));

            if(object.has("commandUsage"))
                command.setCommandUsage(new CommandUsage(object.get("commandUsage").getAsJsonObject()));

            if(object.has("commandMessage"))
                command.setCommandMessage(new CommandMessage(object.get("commandMessage").getAsJsonObject()));
        }
        Logger.log(Level.INFO, "Data assigned.");

        Logger.log(Level.INFO, "Command contents loaded.");
    }

    public void reloadCommandContents() {

        Logger.log(Level.INFO, "Starting command contents reload.");

        loadCommandContents();

        Logger.log(Level.INFO, "All command contents were reloaded.");
    }

    public static CommandManager registerNewCommandManager(SmartCommandsAPI api) {

        if(api.getCommandManager() != null)
            throw new IllegalStateException("CommandManager is already registered.");

        Logger.log(Level.INFO, "Registering new CommandManager...");

        CommandManager commandManager = new CommandManager(api);

        commandManager.setupCommandMap();
        commandManager.loadCommands();
        commandManager.loadCommandContents();

        Logger.log(Level.INFO, "CommandManager registered.");

        return commandManager;
    }
}
