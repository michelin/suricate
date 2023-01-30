/*
 *
 *  * Copyright 2012-2018 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.michelin.suricate.utils.exceptions;

import com.michelin.suricate.model.enums.ApiErrorEnum;

import java.text.MessageFormat;

public class EmailAlreadyExistException extends ApiException {
    /**
     * Message for email already exist
     */
    private static final String MSG_EMAIL_ALREADY_EXIST = "Email {0} already exist";

    /**
     * Constructor
     * @param email The email that already exist
     */
    public EmailAlreadyExistException(String email) {
        super(MessageFormat.format(MSG_EMAIL_ALREADY_EXIST, email), ApiErrorEnum.EMAIL_ALREADY_EXIST);
    }
}
