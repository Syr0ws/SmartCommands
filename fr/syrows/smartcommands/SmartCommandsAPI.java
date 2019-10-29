package fr.syrows.smartcommands;

import com.google.gson.Gson;
import fr.syrows.smartcommands.commands.CommandManager;
import fr.syrows.smartcommands.utils.Logger;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class SmartCommandsAPI {

    public static final Gson gson = new Gson();

    private String commandResourcePath, contentsResourcePath;
    private boolean debug, useContentsFile, createContentsFile;

    private Plugin plugin;
    private Logger logger;

    private SmartCommandsManager smartCommandsManager;
    private CommandManager commandManager;

    public String getCommandResourcePath() {
        return this.commandResourcePath;
    }

    public String getContentsResourcePath() {
        return this.contentsResourcePath;
    }

    public void debug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebuggerEnabled() {
        return this.debug;
    }

    public boolean useContentsFile() {
        return this.useContentsFile;
    }

    public boolean canCreateContentsFile() {
        return this.createContentsFile;
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

        private String commandResourcePath, contentsResourcePath;
        private boolean debug, useContentsFile, createContentsFile;

        public ApiBuilder() {
            this.commandResourcePath = "commands.json";
            this.contentsResourcePath = "command_contents.json";
            this.useContentsFile = true;
            this.createContentsFile = true;
            this.debug = false;
        }

        public ApiBuilder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public ApiBuilder setCommandResourceFolderPath(String path) {
            this.commandResourcePath = String.format("%s/commands.json", path);
            return this;
        }

        public ApiBuilder setContentsResourceFolderPath(String path) {
            this.contentsResourcePath = String.format("%s/command_contents.json", path);
            return this;
        }

        public ApiBuilder doNotUseContentsFile() {
            this.useContentsFile = false;
            return this;
        }

        public ApiBuilder doNotCreateContentsFile() {
            this.createContentsFile = false;
            return this;
        }

        public SmartCommandsAPI build(Plugin plugin) {

            SmartCommandsAPI api = new SmartCommandsAPI();

            api.debug = this.debug;

            api.commandResourcePath = this.commandResourcePath;
            api.contentsResourcePath = this.contentsResourcePath;

            api.useContentsFile = this.useContentsFile;
            api.createContentsFile = this.createContentsFile;

            api.initialize(plugin);

            return api;
        }
    }
}
