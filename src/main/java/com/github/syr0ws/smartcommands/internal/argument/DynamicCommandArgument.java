package com.github.syr0ws.smartcommands.internal.argument;

import com.github.syr0ws.smartcommands.api.argument.DynamicArgumentValueProvider;

import java.util.Optional;

public class DynamicCommandArgument extends AbstractCommandArgument {

    private DynamicArgumentValueProvider provider;

    public DynamicCommandArgument(AbstractCommandArgument parent, String name) {
        super(parent, name);
    }

    public static boolean isDynamicArgument(String name) {
        return name.startsWith("[") && name.endsWith("]");
    }

    public Optional<DynamicArgumentValueProvider> getProvider() {
        return Optional.ofNullable(this.provider);
    }

    public void setProvider(DynamicArgumentValueProvider provider) {
        this.provider = provider;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
