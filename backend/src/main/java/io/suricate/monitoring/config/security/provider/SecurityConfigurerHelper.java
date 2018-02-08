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


package io.suricate.monitoring.config.security.provider;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

/**
 * Interface used to defined default security config
 */
public interface SecurityConfigurerHelper {

    /**
     * Configure method used to apply the default authentication strategy
     * @param auth the authentication manager
     * @throws Exception
     */
    void configure(AuthenticationManagerBuilder auth) throws Exception;
}

