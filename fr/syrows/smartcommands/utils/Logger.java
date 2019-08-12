package fr.syrows.smartcommands.utils;

import fr.syrows.smartcommands.SmartCommandsAPI;

import java.util.logging.Level;

public class Logger {

    public static void log(Level lvl, String msg) {
        SmartCommandsAPI api = SmartCommandsAPI.getApi();
        if(api.isDebuggerEnabled()) api.getPlugin().getLogger().log(lvl, msg);
    }
}
