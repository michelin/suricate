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

import java.util.Collection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * Utils class used to hide widget properties from logs.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ToStringUtils {
    /**
     * Hide the widget properties from the given logs.
     *
     * @param outputLogs             The logs to clear
     * @param widgetPropertiesValues The widget properties values to hide
     * @return The cleared logs without widget properties
     */
    public static String hideWidgetConfigurationInLogs(String outputLogs, Collection<String> widgetPropertiesValues) {
        String clearedLogs = StringUtils.trimToNull(outputLogs);

        if (widgetPropertiesValues != null && clearedLogs != null) {
            for (String widgetPropertiesValue : widgetPropertiesValues) {
                if (widgetPropertiesValue != null) {
                    clearedLogs = clearedLogs.replaceAll(widgetPropertiesValue,
                        StringUtils.leftPad(StringUtils.EMPTY, widgetPropertiesValue.length(), "*"));
                }
            }
        }

        return clearedLogs;
    }
}
