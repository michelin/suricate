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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public final class PropertiesUtils {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtils.class);

    /**
     * Method used to load properties form string
     * @param properties the string containing properties
     * @return the properties object
     */
    public static Properties loadProperties(String properties){
        Properties ret = null;
        if (StringUtils.isNotBlank(properties)) {
            try (StringReader reader = new StringReader(properties)){
                ret = new Properties();
                ret.load(reader);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return ret;
    }

    /**
     * Method used to load properties form file
     * @param filePath the file path containing properties
     * @return the properties object
     */
    public static Properties loadFile(String filePath){
        Properties ret = null;
        InputStream input = null;
        // Check parameter
        if (StringUtils.isBlank(filePath)){
            return ret;
        }
        try {
            input = PropertiesUtils.class.getResourceAsStream(filePath);

            if (input != null){
                ret = new Properties();
                // load a properties file
                ret.load(input);
            }

        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            IOUtils.closeQuietly(input);
        }
        return ret;
    }


    /**
     * Method used to convert string properties to map
     * @param properties properties file with key=value
     * @return a map representing the Tuple key=value
     */
    public static Map<String, String> getMap(String properties){
        Map<String, String> ret = new TreeMap<>();
        Properties prop = loadProperties(properties);
        if (prop != null) {
            for (String name : prop.stringPropertyNames()) {
                ret.put(name, StringUtils.trimToNull(prop.getProperty(name)));
            }
        }
        return ret;
    }

    private PropertiesUtils() {
    }
}
