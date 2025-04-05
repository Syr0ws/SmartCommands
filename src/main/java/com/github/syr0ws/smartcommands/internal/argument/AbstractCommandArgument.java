package com.github.syr0ws.smartcommands.internal.argument;

import com.github.syr0ws.crafter.util.Validate;
import com.github.syr0ws.smartcommands.api.CommandCallable;
import com.github.syr0ws.smartcommands.api.argument.CommandArgument;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractCommandArgument implements CommandArgument {

    private final String name;
    private final String path;
    private final AbstractCommandArgument parent;
    private final Set<AbstractCommandArgument> children;
    private CommandCallable callable;

    public AbstractCommandArgument(AbstractCommandArgument parent, String name) {
        Validate.notNull(name, "name cannot be null");

        this.name = name;
        this.parent = parent; // May be null if current argument is root.
        this.path = parent == null ? name : parent.getPath() + "." + name;
        this.children = new HashSet<>();
    }

    public void addChild(AbstractCommandArgument child) {
        Validate.notNull(child, "child cannot be null");

        AbstractCommandArgument childParent = child.getParent().orElse(null);

        // Ensuring that the parent of the child to add is the current node.
        if (!this.equals(childParent)) {
            throw new IllegalStateException("Child parent is not the current node");
        }

        this.children.add(child);
    }

    public boolean hasChild(String name) {
        Validate.notNull(name, "name cannot be null");
        return this.children.stream()
                .anyMatch(argument -> argument.getName().equals(name));
    }

    public Optional<AbstractCommandArgument> getChild(String name) {
        Validate.notNull(name, "name cannot be null");
        return this.children.stream()
                .filter(argument -> argument.getName().equals(name))
                .findFirst();
    }

    public void setCallable(CommandCallable callable) {
        this.callable = callable;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public Optional<CommandCallable> getCallable() {
        return Optional.ofNullable(this.callable);
    }

    @Override
    public Optional<AbstractCommandArgument> getParent() {
        return Optional.ofNullable(this.parent);
    }

    @Override
    public Set<AbstractCommandArgument> getChildren() {
        return Collections.unmodifiableSet(this.children);
    }

    @Override
    public Set<CommandCallable> getChildCommands() {

        Set<CommandCallable> callables = new HashSet<>();

        if(this.callable != null) {
            callables.add(this.callable);
        }

        for(AbstractCommandArgument child : this.children) {
            callables.addAll(child.getChildCommands());
        }

        return callables;
    }
}
