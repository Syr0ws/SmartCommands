package fr.syrows.smartcommands.commands;

import fr.syrows.smartcommands.SmartCommand;
import fr.syrows.smartcommands.SmartCommandsAPI;
import fr.syrows.smartcommands.SmartCommandsManager;
import fr.syrows.smartcommands.tools.BukkitCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class CommandManager {

    private SmartCommandsAPI api;

    private SimpleCommandMap commandMap;

    public CommandManager(SmartCommandsAPI api) {
        this.api = api;
    }

    public void registerCommand(String name, CommandExecutor executor) {

        name = name.toLowerCase();

        SmartCommandsManager manager = this.api.getSmartCommandsManager();

        this.api.getLogger().log(Level.INFO, String.format("Registering command '%s'...", name));

        if(this.commandMap == null)
            throw new IllegalStateException("CommandMap cannot be null. Initialize the API before registering smartCommands.");

        if(!manager.isLoaded(name))
            throw new NullPointerException(String.format("Command '%s' is not loaded.", name));

        if(executor == null)
            throw new IllegalArgumentException("CommandExecutor cannot be null.");

        SmartCommand command = manager.getSmartCommand(name);
        command.setExecutor(executor);

        BukkitCommand bukkitCommand = new BukkitCommand(command);

        this.commandMap.register(this.api.getPlugin().getName(), bukkitCommand);

        this.api.getLogger().log(Level.INFO, String.format("Command '%s' registered.", name));
    }

    public void unregisterCommand(String plugin, String name) {

        plugin = plugin.toLowerCase();
        name = name.toLowerCase();

        if(!isRegistered(plugin, name))
            throw new NullPointerException(String.format("Command '%s' is not registered.", name));

        Map<String, Command> knownCommands = getKnownCommands();

        Command command = knownCommands.get(name);

        knownCommands.remove(name);
        knownCommands.remove(String.format("%s:%s", plugin, name));

        for(String alias : command.getAliases()) {

            String format = String.format("%s:%s", plugin, alias);

            if(knownCommands.containsKey(format)) {
                knownCommands.remove(alias);
                knownCommands.remove(format);
            }
        }
        this.api.getLogger().log(Level.INFO, String.format("Command '%s' has been unregistered.", name));
    }

    public boolean isRegistered(String plugin, String name) {

        Map<String, Command> knownCommands = getKnownCommands();

        String command = String.format("%s:%s", plugin.toLowerCase(), name);

        return knownCommands.containsKey(command);
    }

    public boolean isRegistered(String name) {
        return this.commandMap.getCommand(name.toLowerCase()) != null;
    }

    public void setupCommandMap() {

        this.api.getLogger().log(Level.INFO, "Initializing CommandMap...");

        try {

            this.commandMap = (SimpleCommandMap) getField(Bukkit.getServer().getPluginManager(), "commandMap");

            this.api.getLogger().log(Level.INFO, "CommandMap initialized.");

        } catch (NoSuchFieldException | IllegalAccessException e) {

            this.api.getLogger().log(Level.SEVERE, "Cannot initialize CommandMap.");

            e.printStackTrace();
        }
    }

    private Map<String, Command> getKnownCommands() {

        Map<String, Command> knownCommands = new HashMap<>();

        try {

            Object map = getField(this.commandMap, "knownCommands");
            knownCommands = (HashMap<String, Command>) map;

        } catch (NoSuchFieldException | IllegalAccessException e) {

            e.printStackTrace();
        }
        return knownCommands;
    }

    private Object getField(Object object, String name) throws NoSuchFieldException, IllegalAccessException {

        Field field = object.getClass().getDeclaredField(name);

        field.setAccessible(true);

        Object result = field.get(object);

        field.setAccessible(false);

        return result;
    }
}
