package fr.syrows.smartcommands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.syrows.smartcommands.commands.CommandManager;
import fr.syrows.smartcommands.utils.Logger;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class SmartCommandsAPI {

    public static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private String commandResourcePath, contentsResourcePath;
    private boolean debug, useContentsFile, createContentsFile;

    private Plugin plugin;
    private Logger logger;

    private CommandManager commandManager;

    public String getCommandResourcePath() {
        return this.commandResourcePath;
    }

    public String getContentsResourcePath() {
        return this.contentsResourcePath;
    }

    public boolean isDebuggerEnabled() {
        return this.debug;
    }

    public boolean useContentsFile() { return this.useContentsFile; }

    public boolean canCreateContentsFile() { return this.createContentsFile; }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public Logger getLogger() { return this.logger; }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    private void initialize(Plugin plugin) {

        this.plugin = plugin;
        this.logger = new Logger(this);

        this.logger.log(Level.INFO, "Initializing SmartCommands API...");

        this.commandManager = CommandManager.registerNewCommandManager(this);

        this.logger.log(Level.INFO, "SmartCommands API initialized.");
    }

    public static class ApiBuilder {

        private String commandResourcePath, contentsResourcePath;
        private boolean debug, useContentsFile, createContentsFile;

        public ApiBuilder() {
            this.commandResourcePath = "commands.json";
            this.contentsResourcePath = "command_contents.json";
            this.debug = false;
            this.useContentsFile = true;
            this.createContentsFile = true;
        }

        public ApiBuilder enableDebugger() {
            this.debug = true;
            return this;
        }

        public ApiBuilder setCommandResourceFolderPath(String resourcePath) {
            this.commandResourcePath = resourcePath + "/commands.json";
            return this;
        }

        public ApiBuilder setContentsResourceFolderPath(String resourcePath) {
            this.contentsResourcePath = resourcePath + "/command_contents.json";
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

            api.commandResourcePath = this.commandResourcePath;
            api.contentsResourcePath = this.contentsResourcePath;

            api.debug = this.debug;
            api.useContentsFile = this.useContentsFile;
            api.createContentsFile = this.createContentsFile;

            api.initialize(plugin);

            return api;
        }
    }
}
