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

package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.setting.SettingResponseDto;
import io.suricate.monitoring.model.entities.Setting;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;

/**
 * Manage the generation DTO/Model objects for Setting class
 */
@Mapper(
    componentModel = "spring",
    uses = {
        AllowedSettingValueMapper.class
    }
)
public abstract class SettingMapper {

    /**
     * Map a setting into a DTO
     *
     * @param setting The setting to map
     * @return The setting as DTO
     */
    @Named("toSettingDTO")
    @Mapping(target = "allowedSettingValues", qualifiedByName = "toAllowedSettingValuesDTOs")
    public abstract SettingResponseDto toSettingDTO(Setting setting);

    /**
     * Map a list of settings into a list of settings DTO
     *
     * @param settings The list of settings to map
     * @return The list of settings as DTOs
     */
    @Named("toSettingsDTOs")
    @IterableMapping(qualifiedByName = "toSettingDTO")
    public abstract List<SettingResponseDto> toSettingsDTOs(List<Setting> settings);
}
