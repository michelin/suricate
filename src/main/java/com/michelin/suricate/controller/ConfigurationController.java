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

package com.michelin.suricate.controller;

import com.michelin.suricate.model.enumeration.AuthenticationProvider;
import com.michelin.suricate.property.ApplicationProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Configuration controller.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Configuration", description = "Configuration Controller")
public class ConfigurationController {
    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Get the authentication providers defined in the backend (database, ldap, social providers, ...).
     *
     * @return The authentication provider
     */
    @Operation(summary = "Get the server configuration for authentication providers (DB, LDAP, Social providers)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK")
    })
    @GetMapping(value = "/v1/configurations/authentication-providers")
    public ResponseEntity<List<AuthenticationProvider>> getAuthenticationProviders() {
        List<AuthenticationProvider> providers = new ArrayList<>();

        if (StringUtils.isNotBlank(applicationProperties.getAuthentication().getProvider())) {
            providers.add(
                AuthenticationProvider.valueOf(applicationProperties.getAuthentication().getProvider().toUpperCase()));
        }

        if (applicationProperties.getAuthentication().getSocialProviders() != null) {
            List<AuthenticationProvider> socialProviders =
                applicationProperties.getAuthentication().getSocialProviders()
                    .stream()
                    .filter(socialProvider -> EnumUtils.isValidEnum(AuthenticationProvider.class,
                        socialProvider.toUpperCase()))
                    .map(socialProvider -> AuthenticationProvider.valueOf(socialProvider.toUpperCase()))
                    .toList();

            providers.addAll(socialProviders);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(providers);
    }
}
