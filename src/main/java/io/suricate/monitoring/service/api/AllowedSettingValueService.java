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

import io.suricate.monitoring.model.entity.setting.AllowedSettingValue;
import io.suricate.monitoring.repository.AllowedSettingValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AllowedSettingValueService {
    /**
     * The allowed setting value repository
     */
    private final AllowedSettingValueRepository allowedSettingValueRepository;

    /**
     * Constructor
     *
     * @param allowedSettingValueRepository The allowedSettingRepository
     */
    @Autowired
    public AllowedSettingValueService(final AllowedSettingValueRepository allowedSettingValueRepository) {
        this.allowedSettingValueRepository = allowedSettingValueRepository;
    }

    /**
     * Get an allowedSetting value by id
     *
     * @param id The id to find
     * @return The related allowed setting value
     */
    public Optional<AllowedSettingValue> getOneById(final Long id) {
        return allowedSettingValueRepository.findById(id);
    }
}
