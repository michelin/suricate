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

import com.michelin.suricate.model.enumeration.ApiErrorEnum;
import java.text.MessageFormat;

/** Username already exist exception. */
public class UsernameAlreadyExistException extends ApiException {
    private static final String MSG_USERNAME_ALREADY_EXIST = "Username ''{0}'' already exist";

    /**
     * Constructor.
     *
     * @param username The email that already exist
     */
    public UsernameAlreadyExistException(String username) {
        super(MessageFormat.format(MSG_USERNAME_ALREADY_EXIST, username), ApiErrorEnum.USERNAME_ALREADY_EXIST);
    }
}
