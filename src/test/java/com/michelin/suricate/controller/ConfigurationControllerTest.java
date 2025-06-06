/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.michelin.suricate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.enumeration.AuthenticationProvider;
import com.michelin.suricate.property.ApplicationProperties;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ConfigurationControllerTest {
    @Mock
    private ApplicationProperties applicationProperties;

    @InjectMocks
    private ConfigurationController configurationController;

    @Test
    void shouldGetAuthenticationProviders() {
        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setProvider("ldap");
        authProperties.setSocialProviders(Collections.singletonList("github"));

        when(applicationProperties.getAuthentication()).thenReturn(authProperties);

        ResponseEntity<List<AuthenticationProvider>> actual = configurationController.getAuthenticationProviders();

        assertEquals(MediaType.APPLICATION_JSON, actual.getHeaders().getContentType());
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertEquals(2, actual.getBody().size());
        assertIterableEquals(List.of(AuthenticationProvider.LDAP, AuthenticationProvider.GITHUB), actual.getBody());
    }

    @Test
    void shouldGetAuthenticationProvidersEmpty() {
        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();

        when(applicationProperties.getAuthentication()).thenReturn(authProperties);

        ResponseEntity<List<AuthenticationProvider>> actual = configurationController.getAuthenticationProviders();

        assertEquals(MediaType.APPLICATION_JSON, actual.getHeaders().getContentType());
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertTrue(actual.getBody().isEmpty());
    }
}
