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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class JsonUtilsTest {
    @Test
    void shouldBeInvalidNull() {
        boolean actual = JsonUtils.isValid(null);
        assertFalse(actual);
    }

    @Test
    void shouldBeInvalidEmpty() {
        boolean actual = JsonUtils.isValid(StringUtils.EMPTY);
        assertFalse(actual);
    }

    @Test
    void shouldBeInvalidFormat() {
        boolean actual = JsonUtils.isValid("{\"test\":0");
        assertFalse(actual);
    }

    @Test
    void shouldBeValid() {
        boolean actual = JsonUtils.isValid("{\"test\":0}");
        assertTrue(actual);
    }
}
