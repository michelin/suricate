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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class ToStringUtilsTest {
    @Test
    void shouldHideWidgetConfigurationInLogsNull() {
        String actual = ToStringUtils.hideWidgetConfigurationInLogs(null, null);
        assertNull(actual);
    }

    @Test
    void shouldHideWidgetConfigurationInLogsEmptyLogs() {
        String actual = ToStringUtils.hideWidgetConfigurationInLogs(StringUtils.EMPTY, null);
        assertNull(actual);
    }

    @Test
    void shouldHideWidgetConfigurationInLogsNoConfig() {
        String actual = ToStringUtils.hideWidgetConfigurationInLogs("test", null);
        assertEquals("test", actual);
    }

    @Test
    void shouldHideWidgetConfigurationInLogsEmptyConfig() {
        String actual = ToStringUtils.hideWidgetConfigurationInLogs(null, Collections.emptyList());
        assertNull(actual);
    }

    @Test
    void shouldHideWidgetConfigurationInLogs() {
        String actual = ToStringUtils
            .hideWidgetConfigurationInLogs("Should hide my password", Collections.singletonList("password"));
        assertEquals("Should hide my ********", actual);
    }

    @Test
    void shouldAvoidNullValue() {
        String actual = ToStringUtils
            .hideWidgetConfigurationInLogs("Should hide my password", Arrays.asList("password", null));
        assertEquals("Should hide my ********", actual);
    }
}
