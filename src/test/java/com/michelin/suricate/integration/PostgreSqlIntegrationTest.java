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

package com.michelin.suricate.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.michelin.suricate.model.entity.AllowedSettingValue;
import com.michelin.suricate.model.entity.Role;
import com.michelin.suricate.model.entity.Setting;
import com.michelin.suricate.model.enumeration.DataTypeEnum;
import com.michelin.suricate.model.enumeration.SettingType;
import com.michelin.suricate.repository.AllowedSettingValueRepository;
import com.michelin.suricate.repository.RoleRepository;
import com.michelin.suricate.repository.SettingRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ActiveProfiles("integration-test-postgresql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostgreSqlIntegrationTest {
    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres")
        .withDatabaseName("suricate")
        .withUsername("sa")
        .withPassword("sa");

    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private AllowedSettingValueRepository allowedSettingValueRepository;

    @Autowired
    private RoleRepository roleRepository;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    void shouldContainsSettings() {
        Optional<List<Setting>> actual = settingRepository.findAllByOrderByDescription();

        assertTrue(actual.isPresent());
        assertEquals(2, actual.get().size());
        assertTrue(actual.get().getFirst().isConstrained());
        assertEquals(DataTypeEnum.COMBO, actual.get().getFirst().getDataType());
        assertEquals(SettingType.LANGUAGE, actual.get().getFirst().getType());
        assertEquals("Language", actual.get().getFirst().getDescription());

        assertTrue(actual.get().get(1).isConstrained());
        assertEquals(DataTypeEnum.COMBO, actual.get().get(1).getDataType());
        assertEquals(SettingType.THEME, actual.get().get(1).getType());
        assertEquals("Theme", actual.get().get(1).getDescription());
    }

    @Test
    void shouldContainsAllowedSettingValues() {
        Optional<List<Setting>> actual = settingRepository.findAllByOrderByDescription();

        List<AllowedSettingValue> actualAllowedSettingValues = new ArrayList<>();
        allowedSettingValueRepository.findAll().forEach(actualAllowedSettingValues::add);

        assertTrue(actual.isPresent());

        assertEquals(4, actualAllowedSettingValues.size());
        assertEquals("Default", actualAllowedSettingValues.getFirst().getTitle());
        assertEquals("default-theme", actualAllowedSettingValues.getFirst().getValue());
        assertTrue(actualAllowedSettingValues.getFirst().isDefault());
        assertEquals(actual.get().get(1), actualAllowedSettingValues.getFirst().getSetting());

        assertEquals("Dark", actualAllowedSettingValues.get(1).getTitle());
        assertEquals("dark-theme", actualAllowedSettingValues.get(1).getValue());
        assertFalse(actualAllowedSettingValues.get(1).isDefault());
        assertEquals(actual.get().get(1), actualAllowedSettingValues.get(1).getSetting());

        assertEquals("English", actualAllowedSettingValues.get(2).getTitle());
        assertEquals("en", actualAllowedSettingValues.get(2).getValue());
        assertTrue(actualAllowedSettingValues.get(2).isDefault());
        assertEquals(actual.get().getFirst(), actualAllowedSettingValues.get(2).getSetting());

        assertEquals("Français", actualAllowedSettingValues.get(3).getTitle());
        assertEquals("fr", actualAllowedSettingValues.get(3).getValue());
        assertFalse(actualAllowedSettingValues.get(3).isDefault());
        assertEquals(actual.get().getFirst(), actualAllowedSettingValues.get(3).getSetting());
    }

    @Test
    void shouldContainsRoles() {
        List<Role> actual = new ArrayList<>();
        roleRepository.findAll().forEach(actual::add);

        assertEquals(2, actual.size());
        assertEquals("Administrator Role", actual.getFirst().getDescription());
        assertEquals("ROLE_ADMIN", actual.getFirst().getName());

        assertEquals("User role", actual.get(1).getDescription());
        assertEquals("ROLE_USER", actual.get(1).getName());
    }
}
