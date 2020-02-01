package fr.syrows.smartcommands;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import fr.syrows.smartcommands.commands.CommandContents;
import fr.syrows.smartcommands.contents.CommandHelp;
import fr.syrows.smartcommands.contents.CommandMessage;
import fr.syrows.smartcommands.contents.CommandUsage;
import fr.syrows.smartcommands.utils.FileUtils;
import fr.syrows.smartcommands.utils.Logger;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;

public class SmartCommandsManager {

    private static final String COMMANDS_FOLDER = "commands"; //Name of the folder created on the server.

    private SmartCommandsAPI api;
    private Map<String, SmartCommand> smartCommands = new HashMap<>();

    public SmartCommandsManager(SmartCommandsAPI api) {
        this.api = api;
    }

    public void reloadCommandContents(String name) {

        if(!isLoaded(name))
            throw new NullPointerException(String.format("Command '%s' does not exist.", name));

        SmartCommand command = getSmartCommand(name);
        setCommandContents(command, true);
    }

    public void reloadCommandContents() {
        this.smartCommands.keySet().forEach(this::reloadCommandContents);
    }

    public List<SmartCommand> getSmartCommands() {
        return new ArrayList<>(this.smartCommands.values());
    }

    public SmartCommand getSmartCommand(String name) {
        return this.smartCommands.getOrDefault(name, null);
    }

    public boolean isLoaded(String name) {
        return this.smartCommands.containsKey(name);
    }

    private Map<String, JsonObject> loadCommands() {

        this.api.getLogger().log(Level.INFO, "Loading commands...");

        String commandResourcePath = this.api.getCommandResourcePath();

        InputStream stream = this.api.getPlugin().getResource(commandResourcePath);

        if(stream == null)
            throw new NullPointerException(String.format("Cannot find the resource 'commands.json' at '%s'.", commandResourcePath));

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        Type type = new TypeToken<Map<String, JsonObject>>(){}.getType();

        Map<String, JsonObject> commands = SmartCommandsAPI.gson.fromJson(reader, type);

        this.api.getLogger().log(Level.INFO, String.format("%d command%s loaded.", commands.size(), commands.size() <= 1 ? "" : "s"));

        try { reader.close();
        } catch (IOException e) { e.printStackTrace(); }

        return commands;
    }

    private void loadCommandContents(SmartCommand command, boolean reload) throws IOException {

        Plugin plugin = this.api.getPlugin();

        FileUtils.createDirectory(this.api, plugin.getDataFolder().toPath());
        FileUtils.createDirectory(this.api, Paths.get(plugin.getDataFolder() + "/" + COMMANDS_FOLDER));

        if(!command.isResourceOnly()) {

            String[] array = command.getResourcePath().split("/");
            String fileName = array[array.length - 1];

            Path path = Paths.get(plugin.getDataFolder() + "/" + COMMANDS_FOLDER + "/" + fileName);

            FileUtils.createFileFromResource(this.api, path, command.getResourcePath(), false);

            assignContents(command, Files.newBufferedReader(path));

        } else if(!reload) {

            InputStream stream = plugin.getResource(command.getResourcePath());

            if(stream == null)
                throw new NullPointerException(String.format("Could not find file at '%s' for the command '%s'.", command.getResourcePath(), command.getName()));

            assignContents(command, new BufferedReader(new InputStreamReader(stream)));
        }
    }

    private void assignContents(SmartCommand command, BufferedReader reader) throws IOException {

        JsonObject object = SmartCommandsAPI.gson.fromJson(reader, JsonObject.class);

        parse(command, object);

        reader.close();
    }

    private void parse(SmartCommand command, JsonObject object) {

        object = object.get(command.getName()).getAsJsonObject();

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

        command.setCommandContents(contents);
    }

    private void setCommandContents(SmartCommand command, boolean reload) {

        Logger logger = this.api.getLogger();

        String action = reload ? "reload" : "load";

        if(command.hasResourcePath()) {

            try {

                loadCommandContents(command, reload);

                logger.log(Level.INFO, String.format("Contents %sed for the command '%s'.", action, command.getName()));

            } catch (IOException e) {

                logger.log(Level.SEVERE, String.format("Could not %s contents for the command '%s'.", action, command.getName()));

                e.printStackTrace();
            }

        } else command.setCommandContents(new CommandContents(null, null, null));
    }

    void loadSmartCommands() {

        Map<String, JsonObject> commands = this.loadCommands();

        for (Map.Entry<String, JsonObject> entry : commands.entrySet()) {

            String name = entry.getKey().toLowerCase();
            JsonObject object = entry.getValue();

            SmartCommand command = SmartCommand.deserialize(name, object);

            setCommandContents(command, false);

            this.smartCommands.put(name, command);
        }
    }
}
