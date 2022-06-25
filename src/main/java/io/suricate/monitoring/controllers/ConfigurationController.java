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

import io.suricate.monitoring.model.enums.AuthenticationProvider;
import io.suricate.monitoring.properties.ApplicationProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Get the authentication providers defined in the backend (database, ldap, social providers, ...)
     * @return The authentication provider
     */
    @ApiOperation(value = "Get the server configuration for authentication providers (DB, LDAP, Social providers)", response = AuthenticationProvider.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = AuthenticationProvider.class)
    })
    @GetMapping(value = "/v1/configurations/authentication-providers")
    public ResponseEntity<List<AuthenticationProvider>> getAuthenticationProviders() {
        List<AuthenticationProvider> providers = new ArrayList<>();

        if (StringUtils.isNotBlank(applicationProperties.getAuthentication().getProvider())) {
            providers.add(AuthenticationProvider.valueOf(applicationProperties.getAuthentication().getProvider().toUpperCase()));
        }

        List<AuthenticationProvider> socialProviders = applicationProperties.getAuthentication().socialProviders
                .stream()
                .filter(socialProvider -> EnumUtils.isValidEnum(AuthenticationProvider.class, socialProvider.toUpperCase()))
                .map(socialProvider -> AuthenticationProvider.valueOf(socialProvider.toUpperCase()))
                .collect(Collectors.toList());

        providers.addAll(socialProviders);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(providers);
    }
}
