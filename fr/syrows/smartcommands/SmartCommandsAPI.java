package fr.syrows.smartcommands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.syrows.smartcommands.commands.CommandManager;
import fr.syrows.smartcommands.utils.Logger;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class SmartCommandsAPI {

    private String commandResourcePath, contentsResourcePath;
    private boolean debug, parseColors, useContentsFile, createContentsFile;
    private char colorChar;

    private Plugin plugin;
    private Gson gson;

    private CommandManager commandManager;

    private static SmartCommandsAPI api;

    public String getCommandResourcePath() {
        return this.commandResourcePath;
    }

    public String getContentsResourcePath() {
        return this.contentsResourcePath;
    }

    public boolean isDebuggerEnabled() {
        return this.debug;
    }

    public boolean isColorParserEnabled() {
        return this.parseColors;
    }

    public boolean useContentsFile() { return this.useContentsFile; }

    public boolean canCreateContentsFile() { return this.createContentsFile; }

    public char getColorChar() {
        return this.colorChar;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public Gson getGson() {
        return this.gson;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public void initialize(Plugin plugin) {

        if(SmartCommandsAPI.api != null)
            throw new IllegalStateException("SmartCommandsAPI is already registered.");

        this.plugin = plugin;

        SmartCommandsAPI.api = this;

        Logger.log(Level.INFO, "Initializing SmartCommands API...");

        this.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        this.commandManager = CommandManager.registerNewCommandManager(this);

        Logger.log(Level.INFO, "SmartCommands API initialized.");
    }

    public static SmartCommandsAPI getApi() { return SmartCommandsAPI.api; }

    public static class ApiBuilder {

        private String commandResourcePath, contentsResourcePath;
        private boolean debug, parseColors, useContentsFile, createContentsFile;
        private char colorChar;

        public ApiBuilder() {
            this.commandResourcePath = "commands.json";
            this.contentsResourcePath = "commands_text.json";
            this.debug = false;
            this.parseColors = true;
            this.useContentsFile = true;
            this.createContentsFile = true;
            this.colorChar = '&';
        }

        public ApiBuilder enableDebugger() {
            this.debug = true;
            return this;
        }

        public ApiBuilder disableColorParser() {
            this.parseColors = false;
            return this;
        }

        public ApiBuilder setColorChar(char colorChar) {
            this.colorChar = colorChar;
            return this;
        }

        public ApiBuilder withCommandResourcePath(String resourcePath) {
            this.commandResourcePath = resourcePath + "commands.json";
            return this;
        }

        public ApiBuilder withContentsResourcePath(String resourcePath) {
            this.contentsResourcePath = resourcePath + "commands_text.json";
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

        public SmartCommandsAPI get() {

            SmartCommandsAPI api = new SmartCommandsAPI();

            api.commandResourcePath = this.commandResourcePath;
            api.contentsResourcePath = this.contentsResourcePath;

            api.debug = this.debug;
            api.parseColors = this.parseColors;
            api.useContentsFile = this.useContentsFile;
            api.createContentsFile = this.createContentsFile;

            api.colorChar = this.colorChar;

            return api;
        }
    }
}
