package com.github.syr0ws.smartcommands.internal.argument;

public class StaticCommandArgument extends AbstractCommandArgument {

    public StaticCommandArgument(AbstractCommandArgument parent, String name) {
        super(parent, name);
    }

    @Override
    public boolean isDynamic() {
        return false;
    }
}
