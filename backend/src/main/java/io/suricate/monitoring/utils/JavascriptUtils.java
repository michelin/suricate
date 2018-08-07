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

import io.suricate.monitoring.model.dto.nashorn.WidgetVariableResponse;
import io.suricate.monitoring.model.enums.WidgetVariableType;
import io.suricate.monitoring.service.nashorn.script.Methods;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JavascriptUtils {

    /**
     * Regex to find loop in script
     */
    private static final String REGEX_LOOP = "\\)\\s*\\{";

    /**
     * This variable is used to store previous widget data to Nashorn job
     */
    public static final String INTERNAL_PREVIOUS_VARIABLE = "SURI_PREVIOUS";

    /**
     * This variable is used to store instance id widget
     */
    public static final String INSTANCE_ID_VARIABLE = "SURI_INSTANCE_ID";

    /**
     * This variable is used to add optional variable
     */
    public static final String OPTIONAL = "OPTIONAL";

    /**
     * Regex to find variable in javascript
     */
    private static final Pattern REGEX_VARIABLE_DOCUMENTED = Pattern.compile("(SURI_[A-Z0-9_]+\\:\\:[^\\r\\n]+)|(SURI_[A-Z0-9_]+)");

    /**
     * Regex for global variable
     */
    private static final Pattern REGEX_GLOBAL_VARIABLE = Pattern.compile("(WIDGET_CONFIG_[A-Z0-9_]+)");


    /**
     * String to inject in script
     */
    private static  final String INJECT_STRING = "Packages."+ Methods.class.getName()+".checkInterupted();";

    private static final int VARIABLE_NAME_INDEX = 0;
    private static final int VARIABLE_TITLE_INDEX = VARIABLE_NAME_INDEX + 1;
    private static final int VARIABLE_TYPE_INDEX = VARIABLE_TITLE_INDEX + 1;
    private static final int VARIABLE_DATA_INDEX = VARIABLE_TYPE_INDEX + 1;
    private static final int VARIABLE_OPTIONAL_INDEX = VARIABLE_DATA_INDEX +1;

    /**
     * The length of the array for a key:value
     */
    private static final int KEY_VALUE_TAB_LENGTH = 2;

    /**
     * Method used to inject interruption in loop for javascript code
     * @param data javascript code
     * @return the javascript code with interruption on it
     */
    public static String injectInterrupt(String data){
        return StringUtils.trimToEmpty(data).replaceAll(REGEX_LOOP,"){"+INJECT_STRING);
    }

    /**
     * Method used to prepare nashorn script and update path
     * @param data javascript script
     * @return the script with all class path updated
     */
    public static String prepare(String data){
        return injectInterrupt(StringUtils.trimToEmpty(data).replace("Packages.","Packages."+Methods.class.getName()+"."));
    }


    /**
     * Method used to extract variable from javascript. Some documentation can be added to the variable like:
     * # SURI_JENKINS_TOKEN::Jenkins token used to authenticate user::STRING
     * @param javascript string content representing javascript
     * @return list of object containing (variable name, description and type)
     */
    public static List<WidgetVariableResponse> extractVariables(String javascript){
        List<WidgetVariableResponse> ret = new ArrayList<>();

        if (StringUtils.isBlank(javascript)){
            return ret;
        }

        Map<String,WidgetVariableResponse> map = new LinkedHashMap<>();
        Matcher matcher = REGEX_VARIABLE_DOCUMENTED.matcher(javascript);
        while (matcher.find()){
            WidgetVariableResponse widgetVariableResponse = new WidgetVariableResponse();
            String data = matcher.group(1);
            if (StringUtils.isNotEmpty(data)) {
                extractDocumentedVariable(widgetVariableResponse, data);
            } else {
                widgetVariableResponse.setName(matcher.group(2));
                widgetVariableResponse.setRequired(true);
            }
            if (!INTERNAL_PREVIOUS_VARIABLE.equals(widgetVariableResponse.getName())
                    && !INSTANCE_ID_VARIABLE.equals(widgetVariableResponse.getName())
                    && (!map.containsKey(widgetVariableResponse.getName()) || StringUtils.isNotBlank(widgetVariableResponse.getDescription()))) {
                map.put(widgetVariableResponse.getName(), widgetVariableResponse);
            }
        }
        ret.addAll(map.values());
        return ret;
    }

    /**
     * Method used to extract documented variable from script file
     * @param widgetVariableResponse variable read
     * @param data the line read
     */
    private static void extractDocumentedVariable(WidgetVariableResponse widgetVariableResponse, String data) {
        String[] array = data.split("\\:\\:");
        widgetVariableResponse.setName(array[VARIABLE_NAME_INDEX]);
        widgetVariableResponse.setDescription(array[VARIABLE_TITLE_INDEX]);
        widgetVariableResponse.setType(EnumUtils.getEnum(WidgetVariableType.class, array[VARIABLE_TYPE_INDEX].toUpperCase()));
        if (array.length > VARIABLE_DATA_INDEX && widgetVariableResponse.getType() != null) {
            switch (widgetVariableResponse.getType()){
                case COMBO:
                    widgetVariableResponse.setValues(parseKeyValue(StringUtils.trimToNull(array[VARIABLE_DATA_INDEX])));
                    break;
                case MULTIPLE:
                    widgetVariableResponse.setValues(parseKeyValue(StringUtils.trimToNull(array[VARIABLE_DATA_INDEX])));
                    break;
                default:
                    widgetVariableResponse.setData(StringUtils.trimToNull(array[VARIABLE_DATA_INDEX]));
                    break;
            }
        }
        if (array.length > VARIABLE_OPTIONAL_INDEX) {
            String optional = array[VARIABLE_OPTIONAL_INDEX];
            if (StringUtils.isBlank(optional) || !OPTIONAL.equals(optional)) {
                widgetVariableResponse.setRequired(true);
            } else {
                widgetVariableResponse.setRequired(false);
            }
        } else {
            widgetVariableResponse.setRequired(true);
        }
    }

    /**
     * Method used to parse key value from string
     * @param content the key value as string (KEY:VALUE,KEY:VALUE, ....)
     * @return a map with key values
     */
    public static Map<String,String> parseKeyValue(String content){
        Map<String, String> ret = null;
        if (StringUtils.isNotBlank(content)) {
            ret = new HashMap<>();
            for (String keyValue : content.split(",")) {
                String tab[] = keyValue.split(":");
                if (tab.length == KEY_VALUE_TAB_LENGTH) {
                    ret.put(tab[0], tab[1]);
                }
            }
        }
        return  ret;
    }

    /**
     * Method used to extract global variable from String.<br/>
     * Global variable mus follow the REGEX ( WIDGET_CONFIG_[A-Z0-9_]+
     * @param content the content to parse
     * @return the list of global variables extracted
     */
    public static List<String> extractGlobalVariable(String content){
        List<String> ret = null;
        if (StringUtils.isNotBlank(content)) {
            ret = new ArrayList<>();
            Matcher matcher = REGEX_GLOBAL_VARIABLE.matcher(content);
            while (matcher.find()) {
                ret.add(matcher.group(1));
            }
        }
        return ret;
    }

    /**
     * Private Constructor
     */
    private JavascriptUtils() {
    }
}
