package com.github.syr0ws.smartcommands.internal.argument;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.smartcommands.api.CommandCallable;
import com.github.syr0ws.smartcommands.api.argument.CommandArgument;
import com.github.syr0ws.smartcommands.api.argument.CommandArgumentTree;
import com.github.syr0ws.smartcommands.api.argument.DynamicArgumentValueProvider;
import com.github.syr0ws.smartcommands.api.Command;
import com.github.syr0ws.smartcommands.api.SmartCommand;
import com.github.syr0ws.smartcommands.api.exception.InvalidCommandMethodException;

import java.util.Optional;

public class SimpleCommandArgumentTree implements CommandArgumentTree {

    private final AbstractCommandArgument root;

    public SimpleCommandArgumentTree(SmartCommand command) {
        Validate.notNull(command, "command cannot be null");
        this.root = new StaticCommandArgument(null, command.getName());
    }

    public void addCommandToTree(CommandCallable callable) {

        String[] args = callable.command().args();

        // The command corresponds to the no arg command.
        if (args.length == 0) {
            this.root.setCallable(callable);
            return;
        }

        AbstractCommandArgument current = this.root;

        for (String arg : args) {

            String argument = arg.toLowerCase();
            Optional<AbstractCommandArgument> optional = current.getChild(argument);

            // Nothing to do, the current argument already exists.
            if (optional.isPresent()) {
                current = optional.get();
                continue;
            }

            // Adding the argument to the tree.
            AbstractCommandArgument child = DynamicCommandArgument.isDynamicArgument(argument) ?
                    new DynamicCommandArgument(current, argument) :
                    new StaticCommandArgument(current, argument);

            current.addChild(child);
            current = child;
        }

        current.setCallable(callable);
    }

    @Override
    public CommandArgument getArgument(String path) {

        path = path.toLowerCase();
        AbstractCommandArgument current = this.root;

        for (String arg : path.split("\\.")) {

            Optional<AbstractCommandArgument> child = current.getChild(arg);

            // If the current node has a corresponding child, matching it with the current argument.
            if (child.isPresent()) {
                current = child.get();
                continue;
            }

            // Otherwise, the argument cannot be matched with a node in the tree.
            return null;
        }

        // If we go here, the arguments have been entirely matched. Returning the leaf node.
        return current;
    }

    @Override
    public Optional<CommandArgument> getArgumentIfExists(String path) {
        CommandArgument argument = this.getArgument(path);
        return Optional.ofNullable(argument);
    }

    @Override
    public Optional<CommandArgument> getArgument(String[] args) {
        Validate.notNull(args, "args cannot be null");

        // In case no argument has been provided, returning the root.
        if (args.length == 0) {
            return Optional.of(this.root);
        }

        AbstractCommandArgument current = this.root;

        for (String s : args) {

            String arg = s.toLowerCase();

            Optional<AbstractCommandArgument> child = current.getChild(arg);

            // If the current node has a corresponding child, matching it with the current argument.
            if (child.isPresent()) {
                current = child.get();
                continue;
            }

            // If the current node has a dynamic child, it is matched with the current argument as
            // a node can have at most one dynamic child.
            child = current.getChildren().stream().filter(CommandArgument::isDynamic).findFirst();

            if (child.isPresent()) {
                current = child.get();
                continue;
            }

            // Otherwise, the argument cannot be matched with a node in the tree.
            return Optional.empty();
        }

        // If we go here, the arguments have been entirely matched. Returning the leaf node.
        return Optional.of(current);
    }

    @Override
    public void addArgumentValueProvider(String path, DynamicArgumentValueProvider provider) {
        Validate.notNull(path, "path cannot be null");
        Validate.notNull(provider, "provider cannot be null");

        CommandArgument argument = this.getArgument(path);

        if (argument == null) {
            throw new InvalidCommandMethodException("Path '%s' does not exist".formatted(path));
        }

        if (!(argument instanceof DynamicCommandArgument)) {
            throw new IllegalStateException("Argument is not dynamic");
        }

        ((DynamicCommandArgument) argument).setProvider(provider);
    }

    @Override
    public CommandArgument getRoot() {
        return this.root;
    }
}
