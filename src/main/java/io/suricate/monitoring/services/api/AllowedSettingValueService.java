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

import io.suricate.monitoring.model.entities.AllowedSettingValue;
import io.suricate.monitoring.repositories.AllowedSettingValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Manager the allowed setting values
 */
@Service
public class AllowedSettingValueService {
    /**
     * The allowed setting value repository
     */
    private final AllowedSettingValueRepository allowedSettingValueRepository;

    /**
     * Constructor
     *
     * @param allowedSettingValueRepository The allowed setting value repository
     */
    @Autowired
    public AllowedSettingValueService(final AllowedSettingValueRepository allowedSettingValueRepository) {
        this.allowedSettingValueRepository = allowedSettingValueRepository;
    }

    /**
     * Get an allowed setting value by ID
     *
     * @param id The ID
     * @return The allowed setting value
     */
    public Optional<AllowedSettingValue> getOneById(final Long id) {
        return allowedSettingValueRepository.findById(id);
    }
}
