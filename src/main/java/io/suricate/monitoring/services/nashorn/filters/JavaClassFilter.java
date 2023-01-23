/*
 *
 *  * Copyright 2012-2021 the original author or authors.
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

package io.suricate.monitoring.services.nashorn.filters;

import io.suricate.monitoring.services.nashorn.script.NashornWidgetScript;
import jdk.nashorn.api.scripting.ClassFilter;

public class JavaClassFilter implements ClassFilter {
    /**
     * Method used to authorize access to some Java class
     * @param s class name to check
     * @return true is the class name is authorized, false otherwise
     */
    public boolean exposeToScripts(String s) {
        return s.compareTo(NashornWidgetScript.class.getName()) == 0;
    }
}
