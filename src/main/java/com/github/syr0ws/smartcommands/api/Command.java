package com.github.syr0ws.smartcommands.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark a method as a command handler.
 * Defines metadata such as command arguments, allowed sender types, and required permissions.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Command {

    /**
     * Specifies the argument path that triggers this command.
     *
     * @return an array of command arguments
     */
    String[] args();

    /**
     * Specifies which types of {@link org.bukkit.command.CommandSender} are allowed to execute this command.
     *
     * @return an array of allowed sender types
     */
    CommandSenderType[] allowedSenders();

    /**
     * Specifies the permission required to execute this command.
     * If empty, no permission is required.
     *
     * @return the required permission string, or an empty string if none is required
     */
    String permission() default "";
}
