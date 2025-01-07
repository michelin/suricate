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

package com.michelin.suricate.service.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.michelin.suricate.property.ApplicationProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonalAccessTokenHelperServiceTest {
    @Mock
    private ApplicationProperties applicationProperties;

    @InjectMocks
    private PersonalAccessTokenHelperService personalAccessTokenHelperService;

    @Test
    void shouldCreatePersonalAccessToken() {
        ApplicationProperties.PersonalAccessToken patProperties = new ApplicationProperties.PersonalAccessToken();
        patProperties.setPrefix("test");

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setPat(patProperties);

        when(applicationProperties.getAuthentication()).thenReturn(authProperties);

        String actual = personalAccessTokenHelperService.createPersonalAccessToken();

        assertTrue(actual.startsWith("test_"));
    }

    @Test
    void shouldComputePersonAccessTokenChecksum() {
        ApplicationProperties.PersonalAccessToken patProperties = new ApplicationProperties.PersonalAccessToken();
        patProperties.setPrefix("test");
        patProperties.setChecksumSecret("secret");

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setPat(patProperties);

        when(applicationProperties.getAuthentication()).thenReturn(authProperties);

        Long actual = personalAccessTokenHelperService
            .computePersonAccessTokenChecksum("test_1NNTKc5hL0Rc83lSwqSV05NSQ0E19R9Pw");

        assertEquals(3008800073L, actual);
    }

    @Test
    void shouldNotValidateTokenBecauseLength() {
        ApplicationProperties.PersonalAccessToken patProperties = new ApplicationProperties.PersonalAccessToken();
        patProperties.setPrefix("test");

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setPat(patProperties);

        boolean actual = personalAccessTokenHelperService.validateToken("1NNTKc5hL0Rc83lSwqSV05NSQ0E19R9Pw");

        assertFalse(actual);
    }

    @Test
    void shouldNotValidateTokenBecausePrefix() {
        ApplicationProperties.PersonalAccessToken patProperties = new ApplicationProperties.PersonalAccessToken();
        patProperties.setPrefix("test");

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setPat(patProperties);

        when(applicationProperties.getAuthentication()).thenReturn(authProperties);

        boolean actual = personalAccessTokenHelperService
            .validateToken("wrongPrefix_1NNTKc5hL0Rc83lSwqSV05NSQ0E19R9Pw");

        assertFalse(actual);
    }

    @Test
    void shouldValidateToken() {
        ApplicationProperties.PersonalAccessToken patProperties = new ApplicationProperties.PersonalAccessToken();
        patProperties.setPrefix("test");

        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setPat(patProperties);

        when(applicationProperties.getAuthentication()).thenReturn(authProperties);

        boolean actual = personalAccessTokenHelperService.validateToken("test_1NNTKc5hL0Rc83lSwqSV05NSQ0E19R9Pw");

        assertTrue(actual);
    }
}
