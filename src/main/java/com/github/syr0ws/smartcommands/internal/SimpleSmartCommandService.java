package com.github.syr0ws.smartcommands.internal;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.smartcommands.api.SmartCommand;
import com.github.syr0ws.smartcommands.api.SmartCommandService;
import com.github.syr0ws.smartcommands.api.exception.InvalidCommandException;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleSmartCommandService implements SmartCommandService {

    private final JavaPlugin plugin;

    public SimpleSmartCommandService(JavaPlugin plugin) {
        Validate.notNull(plugin, "plugin cannot be null");
        this.plugin = plugin;
    }

    @Override
    public void registerCommand(SmartCommand command) {
        Validate.notNull(command, "command cannot be null");

        String name = command.getName();

        if (name == null || name.isBlank()) {
            throw new InvalidCommandException("Command name cannot be null or empty");
        }

        PluginCommand pluginCommand = this.plugin.getCommand(name);

        if (pluginCommand == null) {
            throw new InvalidCommandException("Command '%s' not found in the plugin.yml".formatted(name));
        }

        command.build();

        pluginCommand.setExecutor(command);
        pluginCommand.setTabCompleter(command);
    }
}
