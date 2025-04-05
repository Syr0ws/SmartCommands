package com.github.syr0ws.smartcommands.api;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.smartcommands.internal.SimpleSmartCommandService;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Utility class for the SmartCommand library.
 */
public class SmartCommandLibrary {

    /**
     * Creates a new {@link SmartCommandService} instance for the given plugin.
     *
     * @param plugin the plugin for which the service is being created
     * @return a new {@link SmartCommandService} instance
     */
    public static SmartCommandService createService(JavaPlugin plugin) {
        Validate.notNull(plugin, "plugin cannot be null");
        return new SimpleSmartCommandService(plugin);
    }
}
