package fr.syrows.smartcommands.utils;

import fr.syrows.smartcommands.SmartCommandsAPI;

import java.util.logging.Level;

public class Logger {

    private SmartCommandsAPI api;

    public Logger(SmartCommandsAPI api) {
        this.api = api;
    }

    public void log(Level level, String message) {
        if(this.api.isDebuggerEnabled()) this.api.getPlugin().getLogger().log(level, message);
    }
}
