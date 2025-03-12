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
package com.michelin.suricate.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.michelin.suricate.model.dto.api.token.PersonalAccessTokenResponseDto;
import com.michelin.suricate.model.entity.PersonalAccessToken;
import com.michelin.suricate.model.entity.User;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonalAccessTokenMapperTest {
    @InjectMocks
    private PersonalAccessTokenMapperImpl personalAccessTokenMapper;

    @Test
    void shouldToPersonalAccessTokenDto() {
        PersonalAccessToken personalAccessToken = new PersonalAccessToken();
        personalAccessToken.setId(1L);
        personalAccessToken.setName("name");
        personalAccessToken.setChecksum(10L);
        personalAccessToken.setUser(new User());
        personalAccessToken.setCreatedDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));

        PersonalAccessTokenResponseDto actual =
                personalAccessTokenMapper.toPersonalAccessTokenDto(personalAccessToken, "value");

        assertEquals("value", actual.getValue());
        assertEquals("name", actual.getName());
        assertEquals(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")), actual.getCreatedDate());
    }

    @Test
    void shouldToPersonalAccessTokenNoValueDto() {
        PersonalAccessToken personalAccessToken = new PersonalAccessToken();
        personalAccessToken.setId(1L);
        personalAccessToken.setName("name");
        personalAccessToken.setChecksum(10L);
        personalAccessToken.setUser(new User());
        personalAccessToken.setCreatedDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));

        PersonalAccessTokenResponseDto actual =
                personalAccessTokenMapper.toPersonalAccessTokenNoValueDto(personalAccessToken);

        assertNull(actual.getValue());
        assertEquals("name", actual.getName());
        assertEquals(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")), actual.getCreatedDate());
    }

    @Test
    void shouldToPersonalAccessTokensDtos() {
        PersonalAccessToken personalAccessToken = new PersonalAccessToken();
        personalAccessToken.setId(1L);
        personalAccessToken.setName("name");
        personalAccessToken.setChecksum(10L);
        personalAccessToken.setUser(new User());
        personalAccessToken.setCreatedDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));

        List<PersonalAccessTokenResponseDto> actual =
                personalAccessTokenMapper.toPersonalAccessTokensDtos(Collections.singletonList(personalAccessToken));

        assertNull(actual.getFirst().getValue());
        assertEquals("name", actual.getFirst().getName());
        assertEquals(
                Date.from(Instant.parse("2000-01-01T01:00:00.00Z")),
                actual.getFirst().getCreatedDate());
    }
}
