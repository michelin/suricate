package com.michelin.suricate.util.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * OAuth2 exception thrown when user cannot be loaded properly into database
 * after being authenticated to the database.
 */
public class Oauth2AuthenticationProcessingException extends AuthenticationException {
    /**
     * Constructor.
     *
     * @param msg   The message
     * @param cause The cause
     */
    public Oauth2AuthenticationProcessingException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructor.
     *
     * @param msg The message
     */
    public Oauth2AuthenticationProcessingException(String msg) {
        super(msg);
    }
}
