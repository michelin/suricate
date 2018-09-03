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

package io.suricate.monitoring.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);

    private final CacheManager cacheManager;

    /**
     * Default constructor
     * @param cacheManager application cache manager instance
     */
    @Autowired
    public CacheService(final CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Method used to clear all cache
     */
    public void clearAllCache(){
        // Clear cache
        for (String name : cacheManager.getCacheNames()) {
            cacheManager.getCache(name).clear();
        }
        LOGGER.debug("Cache cleared");
    }

    /**
     * Method used to clear cache by cache name
     * @param cacheName the cache name to clear
     */
    public void clearCache(String cacheName){
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null){
            cache.clear();
            LOGGER.debug("Cache {} cleared", cacheName);
        }
    }
}
