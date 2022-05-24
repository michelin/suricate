/*
 *
 *  * Copyright 2012-2021 the original author or authors.
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

package io.suricate.monitoring.controllers;

import io.suricate.monitoring.model.dto.api.ApplicationPropertiesDto;
import io.suricate.monitoring.services.properties.ApplicationPropertiesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Configuration controller
 */
@RestController
@RequestMapping("/api")
@Api(value = "Configuration Controller", tags = {"Configuration"})
public class ConfigurationController {
    /**
     * The configuration Service
     */
    private final ApplicationPropertiesService applicationPropertiesService;

    /**
     * Constructor
     *
     * @param applicationPropertiesService The application properties service
     */
    @Autowired
    public ConfigurationController(final ApplicationPropertiesService applicationPropertiesService) {
        this.applicationPropertiesService = applicationPropertiesService;
    }

    /**
     * Get the authentication provider defined in the backend (database or ldap)
     * @return The authentication provider
     */
    @ApiOperation(value = "Get the server configuration for authentication provider (DB, LDAP...)", response = ApplicationPropertiesDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = ApplicationPropertiesDto.class)
    })
    @GetMapping(value = "/v1/configurations/authentication-provider")
    public ResponseEntity<ApplicationPropertiesDto> getAuthenticationProvider() {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.applicationPropertiesService.getAuthenticationProvider());
    }
}
