package com.github.syr0ws.smartcommands.api.exception;

/**
 * Exception thrown when an error occurs when calling a command.
 */
public class CommandCallException extends Exception {

    /**
     * @see Exception#Exception(String)
     */
    public CommandCallException(String message) {
        super(message);
    }

    /**
     * @see Exception#Exception(String, Throwable)
     */
    public CommandCallException(String message, Throwable cause) {
        super(message, cause);
    }
}
