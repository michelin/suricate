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

package io.suricate.monitoring.repositories;

import io.suricate.monitoring.model.entities.Setting;
import io.suricate.monitoring.model.enums.SettingType;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository used for request Settings in database
 */
public interface SettingRepository extends CrudRepository<Setting, Long>, JpaSpecificationExecutor<Setting> {

    /**
     * Find setting by id
     *
     * @param id The setting id
     * @return The setting
     */
    Optional<Setting> findById(final Long id);

    /**
     * Find setting by type
     *
     * @param settingType The setting type
     * @return The setting
     */
    Optional<Setting> findByType(final SettingType settingType);

    /**
     * Find settings by description
     *
     * @return The list of the settings
     */
    Optional<List<Setting>> findAllByOrderByDescription();
}
