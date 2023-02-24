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

package com.michelin.suricate.utils;

import com.michelin.suricate.services.nashorn.script.NashornWidgetScript;
import org.apache.commons.lang3.StringUtils;

public final class JavaScriptUtils {

    /**
     * "Packages." constant used in Javascript to call REST API
     */
    private static final String PACKAGES_LITERAL = "Packages.";

    /**
     * Regex to find loop in script
     */
    private static final String REGEX_LOOP = "\\)\\s*\\{";

    /**
     * Name of the variable used to store the widget data from previous execution
     */
    public static final String PREVIOUS_DATA_VARIABLE = "SURI_PREVIOUS";

    /**
     * Name of the variable used to store the widget instance ID
     */
    public static final String WIDGET_INSTANCE_ID_VARIABLE = "SURI_INSTANCE_ID";

    /**
     * Full path to the checkInterrupted Java method
     */
    private static final String INJECT_INTERRUPT_STRING = JavaScriptUtils.PACKAGES_LITERAL + NashornWidgetScript.class.getName() + ".checkInterrupted();";

    /**
     * Constructor
     */
    private JavaScriptUtils() { }

    /**
     * Method used to prepare Nashorn script and update path
     *
     * @param data javascript script
     * @return the script with all class path updated
     */
    public static String prepare(String data) {
        return injectInterrupt(
                StringUtils.trimToEmpty(data).replace(
                        JavaScriptUtils.PACKAGES_LITERAL, JavaScriptUtils.PACKAGES_LITERAL + NashornWidgetScript.class.getName() + "."));
    }

    /**
     * Method used to inject interruption in loop for javascript code
     *
     * @param data javascript code
     * @return the javascript code with interruption on it
     */
    public static String injectInterrupt(String data) {
        return StringUtils.trimToEmpty(data).replaceAll(REGEX_LOOP, "){" + INJECT_INTERRUPT_STRING);
    }
}
