/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
