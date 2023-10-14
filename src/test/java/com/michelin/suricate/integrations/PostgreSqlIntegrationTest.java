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

package com.michelin.suricate.integrations;

import static org.assertj.core.api.Assertions.assertThat;

import com.michelin.suricate.model.entities.AllowedSettingValue;
import com.michelin.suricate.model.entities.Role;
import com.michelin.suricate.model.entities.Setting;
import com.michelin.suricate.model.enums.DataTypeEnum;
import com.michelin.suricate.model.enums.SettingType;
import com.michelin.suricate.repositories.AllowedSettingValueRepository;
import com.michelin.suricate.repositories.RoleRepository;
import com.michelin.suricate.repositories.SettingRepository;
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

        assertThat(actual).isPresent();
        assertThat(actual.get()).hasSize(2);
        assertThat(actual.get().get(0).isConstrained()).isTrue();
        assertThat(actual.get().get(0).getDataType()).isEqualTo(DataTypeEnum.COMBO);
        assertThat(actual.get().get(0).getType()).isEqualTo(SettingType.LANGUAGE);
        assertThat(actual.get().get(0).getDescription()).isEqualTo("Language");

        assertThat(actual.get().get(1).isConstrained()).isTrue();
        assertThat(actual.get().get(1).getDataType()).isEqualTo(DataTypeEnum.COMBO);
        assertThat(actual.get().get(1).getType()).isEqualTo(SettingType.THEME);
        assertThat(actual.get().get(1).getDescription()).isEqualTo("Theme");
    }

    @Test
    void shouldContainsAllowedSettingValues() {
        Optional<List<Setting>> actual = settingRepository.findAllByOrderByDescription();

        List<AllowedSettingValue> actualAllowedSettingValues = new ArrayList<>();
        allowedSettingValueRepository.findAll().forEach(actualAllowedSettingValues::add);

        assertThat(actual).isPresent();

        assertThat(actualAllowedSettingValues).hasSize(4);
        assertThat(actualAllowedSettingValues.get(0).getTitle()).isEqualTo("Default");
        assertThat(actualAllowedSettingValues.get(0).getValue()).isEqualTo("default-theme");
        assertThat(actualAllowedSettingValues.get(0).isDefault()).isTrue();
        assertThat(actualAllowedSettingValues.get(0).getSetting()).isEqualTo(actual.get().get(1));

        assertThat(actualAllowedSettingValues.get(1).getTitle()).isEqualTo("Dark");
        assertThat(actualAllowedSettingValues.get(1).getValue()).isEqualTo("dark-theme");
        assertThat(actualAllowedSettingValues.get(1).isDefault()).isFalse();
        assertThat(actualAllowedSettingValues.get(1).getSetting()).isEqualTo(actual.get().get(1));

        assertThat(actualAllowedSettingValues.get(2).getTitle()).isEqualTo("English");
        assertThat(actualAllowedSettingValues.get(2).getValue()).isEqualTo("en");
        assertThat(actualAllowedSettingValues.get(2).isDefault()).isTrue();
        assertThat(actualAllowedSettingValues.get(2).getSetting()).isEqualTo(actual.get().get(0));

        assertThat(actualAllowedSettingValues.get(3).getTitle()).isEqualTo("Français");
        assertThat(actualAllowedSettingValues.get(3).getValue()).isEqualTo("fr");
        assertThat(actualAllowedSettingValues.get(3).isDefault()).isFalse();
        assertThat(actualAllowedSettingValues.get(3).getSetting()).isEqualTo(actual.get().get(0));
    }

    @Test
    void shouldContainsRoles() {
        List<Role> actual = new ArrayList<>();
        roleRepository.findAll().forEach(actual::add);

        assertThat(actual).hasSize(2);
        assertThat(actual.get(0).getDescription()).isEqualTo("Administrator Role");
        assertThat(actual.get(0).getName()).isEqualTo("ROLE_ADMIN");

        assertThat(actual.get(1).getDescription()).isEqualTo("User role");
        assertThat(actual.get(1).getName()).isEqualTo("ROLE_USER");
    }
}
