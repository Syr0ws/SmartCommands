package com.github.syr0ws.smartcommands.api.exception;

/**
 * Exception thrown when a command method is invalid.
 */
public class InvalidCommandMethodException extends RuntimeException {

    /**
     * @see RuntimeException#RuntimeException(String)
     */
    public InvalidCommandMethodException(String message) {
        super(message);
    }

    /**
     * @see RuntimeException#RuntimeException(String, Throwable)
     */
    public InvalidCommandMethodException(String message, Throwable cause) {
        super(message, cause);
    }
}
