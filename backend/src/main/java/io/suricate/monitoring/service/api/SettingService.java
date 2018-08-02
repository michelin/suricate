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

package io.suricate.monitoring.service.api;

import io.suricate.monitoring.model.entity.setting.Setting;
import io.suricate.monitoring.repository.SettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Setting service that manage the settings
 */
@Service
public class SettingService {

    /**
     * Class logger
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(SettingService.class);

    /**
     * The setting repository
     */
    private final SettingRepository settingRepository;

    /**
     * Constructor
     *
     * @param settingRepository The setting repository to inject
     */
    @Autowired
    public SettingService(final SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    /**
     * Get the full list of settings
     *
     * @return The list of settings
     */
    public Optional<List<Setting>> getAll() {
        return this.settingRepository.findAllByOrderByDescription();
    }
}
