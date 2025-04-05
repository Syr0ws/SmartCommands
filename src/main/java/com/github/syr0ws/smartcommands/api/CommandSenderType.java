package com.github.syr0ws.smartcommands.api;

import com.github.syr0ws.crafter.util.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Represents the type of entity that can send a command.
 */
public enum CommandSenderType {

    /**
     * Represents a console sender.
     */
    CONSOLE,

    /**
     * Represents a player sender.
     */
    PLAYER;

    /**
     * Determines the {@link CommandSenderType} of a given {@link CommandSender}.
     *
     * @param sender the command sender to evaluate
     * @return {@code PLAYER} if the sender is a {@link Player}; otherwise {@code CONSOLE}
     */
    public static CommandSenderType getType(CommandSender sender) {
        Validate.notNull(sender, "sender cannot be null");
        return sender instanceof Player ? PLAYER : CONSOLE;
    }
}
