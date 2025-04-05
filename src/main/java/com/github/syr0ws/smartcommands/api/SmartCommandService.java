package com.github.syr0ws.smartcommands.api;

/**
 * Service interface for managing and registering {@link SmartCommand} instances.
 */
public interface SmartCommandService {

    /**
     * Registers the given {@link SmartCommand}.
     *
     * @param command the command to register
     * @throws NullPointerException if the command is {@code null}
     */
    void registerCommand(SmartCommand command);
}
