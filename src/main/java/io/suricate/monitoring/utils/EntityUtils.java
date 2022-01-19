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

package io.suricate.monitoring.utils;

import io.suricate.monitoring.model.entities.generic.AbstractEntity;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

public final class EntityUtils {

    /**
     * Method used to get Lazy Entity id without create a new request
     * @param entity the proxied entity
     * @param <T> the return type
     * @return the entity Id
     */
    public static <T> T getProxiedId(AbstractEntity entity) {
        if (entity instanceof HibernateProxy) {
            LazyInitializer lazyInitializer = ((HibernateProxy) entity).getHibernateLazyInitializer();
            if (lazyInitializer.isUninitialized()) {
                return (T) lazyInitializer.getIdentifier();
            }
        }
        return entity != null ? (T) entity.getId() : null;
    }

    /**
     * Private constructor
     */
    private EntityUtils() {
    }
}
