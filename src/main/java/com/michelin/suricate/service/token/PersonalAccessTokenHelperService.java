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

package com.michelin.suricate.service.token;

import com.michelin.suricate.property.ApplicationProperties;
import io.seruco.encoding.base62.Base62;
import java.security.SecureRandom;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Personal access token helper service.
 */
@Slf4j
@Service
public class PersonalAccessTokenHelperService {
    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Create personal access token.
     *
     * @return A random string
     */
    public String createPersonalAccessToken() {
        // Generate Base62 string of 32 chars length
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);

        Base62 base62 = Base62.createInstance();
        final String randomString = new String(base62.encode(bytes));

        return applicationProperties.getAuthentication().getPat().getPrefix() + "_" + randomString;
    }

    /**
     * Compute the checksum of the personal access token.
     *
     * @param personalAccessToken The personal access token
     * @return The checksum
     */
    public Long computePersonAccessTokenChecksum(String personalAccessToken) {
        Checksum crc32 = new CRC32();
        crc32.update(applicationProperties.getAuthentication().getPat().getChecksumSecret().getBytes(),
            0, applicationProperties.getAuthentication().getPat().getChecksumSecret().length());

        crc32.update(personalAccessToken.getBytes(), 0, personalAccessToken.length());
        return crc32.getValue();
    }

    /**
     * Validate a given personal access token.
     *
     * @param personalAccessToken The personal access token
     * @return true if it is valid
     */
    public boolean validateToken(String personalAccessToken) {
        String[] splitPersonalAccessToken = personalAccessToken.split("_");
        if (splitPersonalAccessToken.length != 2
            || !splitPersonalAccessToken[0].equals(applicationProperties.getAuthentication().getPat().getPrefix())) {
            log.error("Invalid personal access token format");
            return false;
        }

        return true;
    }
}
