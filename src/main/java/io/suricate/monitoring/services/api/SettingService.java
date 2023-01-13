/*
 * Copyright 2012-2021 the original author or authors.
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

package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.entities.Setting;
import io.suricate.monitoring.repositories.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SettingService {
    @Autowired
    private SettingRepository settingRepository;

    /**
     * Get setting by id
     * @param settingId The setting id
     * @return The setting
     */
    @Transactional
    public Optional<Setting> getOneById(final Long settingId) {
        return settingRepository.findById(settingId);
    }

    /**
     * Get settings by description
     * @return The list of the settings
     */
    @Transactional
    public Optional<List<Setting>> getAll() {
        return settingRepository.findAllByOrderByDescription();
    }
}
