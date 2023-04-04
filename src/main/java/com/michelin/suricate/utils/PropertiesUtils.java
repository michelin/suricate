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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
public final class PropertiesUtils {
    private PropertiesUtils() { }

    /**
     * Convert widget parameters values from string to map
     * @param widgetProperties the string containing the widget parameters values (key1=value1)
     * @return The widget parameters values as map
     */
    public static Map<String, String> convertStringWidgetPropertiesToMap(String widgetProperties) {
        Map<String, String> mappedWidgetProperties = new TreeMap<>();
        Properties properties = convertStringWidgetPropertiesToProperties(widgetProperties);

        if (properties != null) {
            for (String propertyName : properties.stringPropertyNames()) {
                if (!properties.getProperty(propertyName).trim().isEmpty()) {
                    mappedWidgetProperties.put(propertyName, properties.getProperty(propertyName));
                }
            }
        }

        return mappedWidgetProperties;
    }

    /**
     * Convert widget parameters values from string to map
     * @param widgetProperties the string containing the widget parameters values (key1=value1)
     * @return The widget parameters values as map
     */
    public static Map<String, String> convertAndDecodeStringWidgetPropertiesToMap(String widgetProperties) {
        return convertStringWidgetPropertiesToMap(widgetProperties)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                    try {
                        return URLDecoder.decode(e.getValue(), "UTF-8");
                    } catch (UnsupportedEncodingException ex) {
                        log.error("An error has occurred decoding widget parameter {}", e, ex);
                    }
                    return e.getValue();
                }));
    }

    /**
     * Convert widget parameters values from string to Properties
     *
     * @param widgetProperties the string containing the widget parameters values
     * @return The widget parameters values as Properties
     */
    public static Properties convertStringWidgetPropertiesToProperties(String widgetProperties) {
        Properties properties = null;

        if (StringUtils.isNotBlank(widgetProperties)) {
            try (StringReader reader = new StringReader(widgetProperties)){
                properties = new Properties();
                properties.load(reader);
            } catch (IOException e) {
                log.error("An error has occurred converting widget parameters values from string to properties: {}", widgetProperties, e);
            }
        }

        return properties;
    }
}
