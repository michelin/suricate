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

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Spring context utils.
 */
@Lazy(false)
@Component
public class SpringContextUtils implements ApplicationContextAware {
    @Getter
    private static ApplicationContext applicationContext;

    /**
     * Set application context.
     *
     * @param applicationContext The application context to set
     */
    @Override
    public void setApplicationContext(@NotNull final ApplicationContext applicationContext) {
        SpringContextUtils.applicationContext = applicationContext;
    }
}
