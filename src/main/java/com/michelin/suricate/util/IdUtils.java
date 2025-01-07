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

package com.michelin.suricate.util;

import com.michelin.suricate.util.exception.ProjectTokenInvalidException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.StringEncryptor;

/**
 * Id utils.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IdUtils {
    /**
     * Method used to decode id without salt.
     *
     * @param token token to decode
     * @return the decoded id or null
     */
    public static Long decrypt(String token) {
        StringEncryptor stringEncryptor =
            (StringEncryptor) SpringContextUtils.getApplicationContext().getBean("noSaltEncryptor");
        long id;
        try {
            id = Long.parseLong(stringEncryptor.decrypt(token));
        } catch (Exception e) {
            log.debug(e.getMessage(), e);
            throw new ProjectTokenInvalidException(token);
        }
        return id;
    }

    /**
     * Method used to encrypt id without salt.
     *
     * @param id to encrypt
     * @return the encrypted id
     */
    public static String encrypt(Long id) {
        StringEncryptor stringEncryptor =
            (StringEncryptor) SpringContextUtils.getApplicationContext().getBean("noSaltEncryptor");
        String token = null;
        if (id != null) {
            try {
                token = stringEncryptor.encrypt(String.valueOf(id));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return token;
    }
}
