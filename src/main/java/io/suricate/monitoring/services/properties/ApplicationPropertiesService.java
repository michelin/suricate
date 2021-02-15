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

package io.suricate.monitoring.services.properties;

import io.suricate.monitoring.configuration.ApplicationProperties;
import io.suricate.monitoring.model.dto.api.ApplicationPropertiesDto;
import org.springframework.stereotype.Service;

/**
 * Manage the application properties
 */
@Service
public class ApplicationPropertiesService {

    /**
     * The application properties
     */
    private final ApplicationProperties applicationProperties;

    /**
     * Constructor
     *
     * @param applicationProperties The application properties
     */
    public ApplicationPropertiesService(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    /**
     * Get the server configuration properties
     *
     * @return The list of useful server configuration properties
     */
    public ApplicationPropertiesDto getAuthenticationProvider() {
        return new ApplicationPropertiesDto("authentication.provider",
                applicationProperties.authentication.provider, "The user provider source (Database or LDAP)");
    }
}
