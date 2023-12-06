/*
 *
 *  * Copyright 2012-2018 the original author or authors.
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

package com.michelin.suricate.services.cache;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

@ExtendWith(MockitoExtension.class)
class CacheServiceTest {
    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @Mock
    private Cache cacheTwo;

    @InjectMocks
    private CacheService cacheService;

    @Test
    void shouldClearAllCaches() {
        when(cacheManager.getCacheNames())
            .thenReturn(Arrays.asList("cache", "cacheTwo"));

        when(cacheManager.getCache(any()))
            .thenReturn(cache)
            .thenReturn(cacheTwo);

        cacheService.clearAllCache();

        verify(cache).clear();
        verify(cacheTwo).clear();
    }

    @Test
    void shouldClearCache() {
        when(cacheManager.getCache(any()))
            .thenReturn(cache);

        cacheService.clearCache("cache");

        verify(cache).clear();
    }

    @Test
    void shouldNotClearCacheIfEmpty() {
        when(cacheManager.getCache(any()))
            .thenReturn(null);

        cacheService.clearCache("cache");

        verify(cache, times(0)).clear();
    }
}
