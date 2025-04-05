package com.github.syr0ws.smartcommands.api.argument;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * Functional interface for providing dynamic argument values at runtime.
 * Typically used for suggesting tab-completion options or resolving dynamic input values
 * based on the command sender.
 */
@FunctionalInterface
public interface DynamicArgumentValueProvider {

    /**
     * Provides a list of possible values for a command argument, based on the given sender.
     *
     * @param sender the command sender requesting the values
     * @return a list of possible argument values
     */
    List<String> provide(CommandSender sender);
}
