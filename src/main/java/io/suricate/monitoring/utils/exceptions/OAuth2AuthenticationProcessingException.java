package io.suricate.monitoring.utils.exceptions;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

/**
 * OAuth2 exception thrown when user cannot be loaded properly into database
 * after being authenticated to the database
 */
public class OAuth2AuthenticationProcessingException extends AuthenticationException {
    /**
     * Constructor
     * @param msg The message
     * @param cause The cause
     */
    public OAuth2AuthenticationProcessingException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructor
     * @param msg The message
     */
    public OAuth2AuthenticationProcessingException(String msg) {
        super(msg);
    }
}
