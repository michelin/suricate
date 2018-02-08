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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import javax.persistence.Entity;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ToStringUtils {

    /**
     * Method used to generate to String an excluding all sub entity
     * @return
     */
    public static String toStringEntity(final Object object) {
        if (object == null) {
            return null;
        }
        List<String> excludeFieldNames = new ArrayList<>();
        final Field[] fields = object.getClass().getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        for (final Field field : fields) {
            Type type = field.getGenericType();
            if (type instanceof ParameterizedType) {
                type = ((ParameterizedType) type).getActualTypeArguments()[0];
            } else if (((Class<?>)type).isArray()){
                type = ((Class<?>)type).getComponentType();
            }
            // Remove entity class
            if (((Class<?>)type).isAnnotationPresent(Entity.class)){
                excludeFieldNames.add(field.getName());
            }
        }
        return new ReflectionToStringBuilder(object)
                .setExcludeFieldNames(excludeFieldNames.toArray(new String[excludeFieldNames.size()]))
                .toString();
    }

    /**
     * Method used to hide all configuration from logs
     * @param log the output log
     * @param values the values to mask
     * @return the logs without any configuration
     */
    public static String hideConfig(String log, Collection<String> values){
        String ret = StringUtils.trimToNull(log);
        if (values != null && ret != null) {
            for (String val : values) {
                if (val != null) {
                    ret = ret.replaceAll(val, StringUtils.leftPad(StringUtils.EMPTY, val.length(), "*"));
                }
            }
        }
        return ret;
    }

    /**
     * Private constructor
     */
    private ToStringUtils() {
    }
}
