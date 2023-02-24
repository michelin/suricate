package com.michelin.suricate.utils.exceptions;

import lombok.Getter;

/**
 * Fatal exception throw when something went wrong in the configuration
 */
@Getter
public class ConfigurationException extends RuntimeException {

    /**
     * Configuration property field name
     */
    private final String propertyName;

    /**
     * Constructor
     *
     * @param message      Error Message
     * @param propertyName property name
     */
    public ConfigurationException(String message, String propertyName) {
        super(message);
        this.propertyName = propertyName;
    }
}
