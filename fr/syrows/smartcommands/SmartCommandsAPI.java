package fr.syrows.smartcommands;

import com.google.gson.Gson;
import fr.syrows.smartcommands.commands.CommandManager;
import fr.syrows.smartcommands.utils.Logger;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class SmartCommandsAPI {

    public static final Gson gson = new Gson();

    private String commandResourcePath;
    private boolean debug;

    private Plugin plugin;
    private Logger logger;

    private SmartCommandsManager smartCommandsManager;
    private CommandManager commandManager;

    public String getCommandResourcePath() {
        return this.commandResourcePath;
    }

    public void debug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebuggerEnabled() {
        return this.debug;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public SmartCommandsManager getSmartCommandsManager() {
        return this.smartCommandsManager;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    private void initialize(Plugin plugin) {

        this.plugin = plugin;
        this.logger = new Logger(this);

        this.logger.log(Level.INFO, "Initializing SmartCommands API...");

        this.smartCommandsManager = new SmartCommandsManager(this);
        this.smartCommandsManager.loadSmartCommands();

        this.commandManager = new CommandManager(this);
        this.commandManager.setupCommandMap();

        this.logger.log(Level.INFO, "SmartCommands API initialized.");
    }

    public static class ApiBuilder {

        private String commandsFilePath;
        private boolean debug;

        public ApiBuilder() {
            this.commandsFilePath = "commands.json";
            this.debug = false;
        }

        public ApiBuilder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public ApiBuilder setCommandsFilePath(String path) {
            this.commandsFilePath = String.format("%s/commands.json", path);
            return this;
        }

        public SmartCommandsAPI build(Plugin plugin) {

            SmartCommandsAPI api = new SmartCommandsAPI();

            api.debug = this.debug;
            api.commandResourcePath = this.commandsFilePath;

            api.initialize(plugin);

            return api;
        }
    }
}
