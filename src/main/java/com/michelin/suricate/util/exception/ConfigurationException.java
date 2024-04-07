package com.michelin.suricate.util.exception;

import lombok.Getter;

/**
 * Configuration exception.
 */
@Getter
public class ConfigurationException extends RuntimeException {
    private final String propertyName;

    /**
     * Constructor.
     *
     * @param message      Error Message
     * @param propertyName property name
     */
    public ConfigurationException(String message, String propertyName) {
        super(message);
        this.propertyName = propertyName;
    }
}
