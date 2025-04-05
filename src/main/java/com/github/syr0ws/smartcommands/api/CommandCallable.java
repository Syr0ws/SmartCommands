package com.github.syr0ws.smartcommands.api;

import com.github.syr0ws.smartcommands.api.exception.CommandCallException;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Represents a callable command bound to a specific {@link SmartCommand}.
 */
public record CommandCallable(SmartCommand owner, Command command, Method method) {

    /**
     * Invokes the code associated with the command.
     *
     * @param context the context for command execution
     * @throws CommandCallException if the command invocation fails
     */
    public void call(CommandExecutionContext context) throws CommandCallException {

        boolean isAccessible = this.method.canAccess(this.owner);

        try {
            this.method.setAccessible(true);
            this.method.invoke(this.owner, context);
        } catch (ReflectiveOperationException exception) {
            throw new CommandCallException("An error occurred while calling the command", exception);
        } finally {
            this.method.setAccessible(isAccessible);
        }
    }

    /**
     * Determines whether the given sender is allowed to execute this command
     * based on the allowed sender types of the command.
     *
     * @param sender the command sender
     * @return {@code true} if the sender type is allowed;
     * {@code false} otherwise
     */
    public boolean isAllowedSender(CommandSender sender) {
        CommandSenderType senderType = CommandSenderType.getType(sender);
        return Arrays.stream(this.command.allowedSenders()).anyMatch(type -> type == senderType);
    }

    /**
     * Checks whether the given sender has permission to execute this command.
     *
     * @param sender the command sender
     * @return {@code true} if the sender has the required permission or if no permission is required;
     * {@code false} otherwise
     */
    public boolean hasPermission(CommandSender sender) {
        String permission = this.command.permission();
        return permission.isBlank() || sender.hasPermission(permission);
    }

    /**
     * Determines whether the given sender can call the command,
     * considering both permission and sender type.
     *
     * @param sender the command sender
     * @return {@code true} if the sender is allowed and has the necessary permission;
     * {@code false} otherwise
     */
    public boolean canCall(CommandSender sender) {
        return this.isAllowedSender(sender) && this.hasPermission(sender);
    }
}
