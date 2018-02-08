/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.suricate.monitoring.utils;

import io.suricate.monitoring.controllers.api.exception.ApiException;
import io.suricate.monitoring.model.dto.error.ApiError;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IdUtils {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(IdUtils.class);

    /**
     * Method used to decode id without salt
     * @param token token to decode
     * @return the decoded id or null
     */
    public static Long decrypt(String token) {
        StringEncryptor stringEncryptor = (StringEncryptor) SpringContextHolder.getApplicationContext().getBean("noSaltEncrypter");
        Long id = null;
        try{
            id = Long.parseLong(stringEncryptor.decrypt(token));
        } catch (Exception e){
            LOGGER.debug(e.getMessage(), e);
            throw new ApiException(ApiError.TOKEN_INVALID);
        }
        return id;
    }

    /**
     * Method used to encrypt id without salt
     * @param id to encrypt
     * @return the encrypted id
     */
    public static String encrypt(Long id) {
        StringEncryptor stringEncryptor = (StringEncryptor) SpringContextHolder.getApplicationContext().getBean("noSaltEncrypter");
        String token = null;
        if (id != null) {
            try {
                token = stringEncryptor.encrypt(String.valueOf(id));
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return token;
    }

    /**
     * Private constructor
     */
    private IdUtils() {
    }
}
