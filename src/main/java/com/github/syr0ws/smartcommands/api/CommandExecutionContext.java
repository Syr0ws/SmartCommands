package com.github.syr0ws.smartcommands.api;

import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.Map;

/**
 * Represents the context in which a command is executed.
 * Holds the sender, command label, and resolved argument values.
 *
 * @param sender    the entity that executed the command
 * @param label     the command label used
 * @param arguments a map of argument names to their corresponding values
 */
public record CommandExecutionContext(CommandSender sender, String label, Map<String, String> arguments) {

    /**
     * Constructs a new {@code CommandExecutionContext} with the given sender, label, and arguments.
     *
     * @param sender    the entity that issued the command
     * @param label     the command label used
     * @param arguments a map of argument names to their values
     */
    public CommandExecutionContext(CommandSender sender, String label, Map<String, String> arguments) {
        this.sender = sender;
        this.label = label;
        this.arguments = Collections.unmodifiableMap(arguments);
    }

    /**
     * Retrieves the value of a specified argument.
     *
     * @param argument the name of the argument
     * @return the value of the argument
     * @throws IllegalArgumentException if the argument is not present in the context
     */
    public String getArgumentValue(String argument) {

        if (!this.arguments.containsKey(argument)) {
            throw new IllegalArgumentException("Argument '%s' not found".formatted(argument));
        }

        return this.arguments.get(argument);
    }
}
