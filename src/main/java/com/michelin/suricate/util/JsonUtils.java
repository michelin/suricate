/*
 * Copyright 2012-2021 the original author or authors.
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

package com.michelin.suricate.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Json utils.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonUtils {
    /**
     * Validate the JSON parameter.
     *
     * @param jsonInString the json string to test
     * @return true if the json is isValid false otherwise
     */
    public static boolean isValid(String jsonInString) {
        if (StringUtils.isNotBlank(jsonInString)) {
            try {
                final ObjectMapper mapper = new ObjectMapper();
                mapper.readTree(jsonInString);
                return true;
            } catch (IOException e) {
                // do nothing
                log.trace(e.getMessage(), e);
            }
        }
        return false;
    }
}
