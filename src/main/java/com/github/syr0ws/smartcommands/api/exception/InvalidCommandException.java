package com.github.syr0ws.smartcommands.api.exception;

/**
 * Exception thrown when a command is invalid.
 */
public class InvalidCommandException extends RuntimeException {

    /**
     * @see RuntimeException#RuntimeException(String)
     */
    public InvalidCommandException(String message) {
        super(message);
    }

    /**
     * @see RuntimeException#RuntimeException(String, Throwable)
     */
    public InvalidCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
