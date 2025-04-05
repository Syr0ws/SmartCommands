package com.github.syr0ws.smartcommands.api.argument;

import com.github.syr0ws.smartcommands.api.CommandCallable;

import java.util.Optional;
import java.util.Set;

/**
 * Represents a command argument in a command structure.
 */
public interface CommandArgument {

    /**
     * Returns the name of this argument.
     *
     * @return the name of the argument
     */
    String getName();

    /**
     * Returns the full path of this argument within the command hierarchy.
     *
     * @return the path of the argument
     */
    String getPath();

    /**
     * Indicates whether this argument is dynamic.
     * Dynamic arguments typically accept variable input from users.
     *
     * @return {@code true} if the argument is dynamic; {@code false} otherwise
     */
    boolean isDynamic();

    /**
     * Returns the {@link CommandCallable} associated with this argument, if any.
     *
     * @return an {@link Optional} containing the callable if present; otherwise an empty {@link Optional}
     */
    Optional<CommandCallable> getCallable();

    /**
     * Returns the parent argument of this argument, if it is not the root argument.
     *
     * @return an {@link Optional} containing the parent argument if present; otherwise an empty {@link Optional}
     */
    Optional<? extends CommandArgument> getParent();

    /**
     * Returns the child arguments for this argument.
     *
     * @return a set of child arguments
     */
    Set<? extends CommandArgument> getChildren();

    /**
     * Returns the callable commands in the arguments hierarchy from the current argument.
     *
     * @return a set of callable commands
     */
    Set<CommandCallable> getChildCommands();
}
