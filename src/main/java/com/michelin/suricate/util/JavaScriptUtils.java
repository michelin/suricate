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

import com.michelin.suricate.service.js.script.JsEndpoints;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/** Javascript utils. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JavaScriptUtils {
    /** Name of the variable used to store the widget data from previous execution. */
    public static final String PREVIOUS_DATA_VARIABLE = "SURI_PREVIOUS";

    /** Name of the variable used to store the widget instance ID. */
    public static final String WIDGET_INSTANCE_ID_VARIABLE = "SURI_INSTANCE_ID";

    /** "Packages." constant used in Javascript to call REST API. */
    private static final String PACKAGES_LITERAL = "Packages.";

    /** Regex to find loop in script. */
    private static final String REGEX_LOOP = "\\)\\s*\\{";

    /** Full path to the checkInterrupted Java method. */
    private static final String INJECT_INTERRUPT_STRING =
            JavaScriptUtils.PACKAGES_LITERAL + JsEndpoints.class.getName() + ".checkInterrupted();";

    /**
     * Method used to prepare Js execution and update path.
     *
     * @param data javascript script
     * @return the script with all class path updated
     */
    public static String prepare(String data) {
        return injectInterrupt(StringUtils.trimToEmpty(data)
                .replace(
                        JavaScriptUtils.PACKAGES_LITERAL,
                        JavaScriptUtils.PACKAGES_LITERAL + JsEndpoints.class.getName() + "."));
    }

    /**
     * Method used to inject interruption in loop for javascript code.
     *
     * @param data javascript code
     * @return the javascript code with interruption on it
     */
    public static String injectInterrupt(String data) {
        return StringUtils.trimToEmpty(data).replaceAll(REGEX_LOOP, "){" + INJECT_INTERRUPT_STRING);
    }
}
