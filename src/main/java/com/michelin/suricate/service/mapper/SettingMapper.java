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

package com.michelin.suricate.service.mapper;

import com.michelin.suricate.model.dto.api.setting.SettingResponseDto;
import com.michelin.suricate.model.entity.Setting;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * Setting mapper.
 */
@Mapper(
    componentModel = "spring",
    uses = {
        AllowedSettingValueMapper.class
    },
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class SettingMapper {
    /**
     * Map a setting into a DTO.
     *
     * @param setting The setting to map
     * @return The setting as DTO
     */
    @Named("toSettingDto")
    @Mapping(target = "allowedSettingValues", qualifiedByName = "toAllowedSettingValuesDtos")
    public abstract SettingResponseDto toSettingDto(Setting setting);

    /**
     * Map a list of settings into a list of settings DTO.
     *
     * @param settings The list of settings to map
     * @return The list of settings as DTOs
     */
    @Named("toSettingsDtos")
    @IterableMapping(qualifiedByName = "toSettingDto")
    public abstract List<SettingResponseDto> toSettingsDtos(List<Setting> settings);
}
