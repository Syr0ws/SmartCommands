package com.github.syr0ws.smartcommands.api.argument;

import java.util.Optional;

/**
 * Represents a tree structure of command arguments.
 * Provides access to the root argument and methods to retrieve or modify arguments by path.
 */
public interface CommandArgumentTree {

    /**
     * Returns the root argument of the command tree.
     *
     * @return the root {@link CommandArgument}
     */
    CommandArgument getRoot();

    /**
     * Retrieves a command argument based on the specified dot-delimited path.
     *
     * @param path the hierarchical path to the argument, with each component separated by a dot
     * @return the corresponding {@link CommandArgument}, or {@code null} if not found
     */
    CommandArgument getArgument(String path);

    /**
     * Retrieves a command argument based on the specified dot-delimited path, if it exists.
     *
     * @param path the hierarchical path to the argument, with each component separated by a dot
     * @return an {@link Optional} containing the found {@link CommandArgument}, or an empty {@link Optional} if not found
     */
    Optional<CommandArgument> getArgumentIfExists(String path);

    /**
     * Retrieves a command argument based on a user input.
     *
     * @param args an array of strings representing the arguments the user specified
     * @return an {@link Optional} containing the found {@link CommandArgument}, or an empty {@link Optional} if not found
     */
    Optional<CommandArgument> getArgument(String[] args);

    /**
     * Associates a {@link DynamicArgumentValueProvider} with a specific argument path.
     * This provider is used to dynamically resolve values for the specified argument.
     *
     * @param path     the dot-delimited path of the argument
     * @param provider the provider to associate with the argument
     */
    void addArgumentValueProvider(String path, DynamicArgumentValueProvider provider);
}
