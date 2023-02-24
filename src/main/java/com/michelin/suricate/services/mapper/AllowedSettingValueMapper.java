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

package com.michelin.suricate.services.mapper;

import com.michelin.suricate.model.dto.api.setting.AllowedSettingValueResponseDto;
import com.michelin.suricate.model.entities.AllowedSettingValue;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.List;

/**
 * Manage the generation DTO/Model objects for AllowedSettingValue class
 */
@Mapper(componentModel = "spring")
public abstract class AllowedSettingValueMapper {

    /**
     * Map an allowed setting value into a DTO
     *
     * @param allowedSettingValue The allowed setting value to map
     * @return The allowed setting value as DTO
     */
    @Named("toAllowedSettingValueDTO")
    public abstract AllowedSettingValueResponseDto toAllowedSettingValueDTO(AllowedSettingValue allowedSettingValue);

    /**
     * Map a list of allowed setting values into a list of DTO
     *
     * @param allowedSettingValues The list of allowed setting values to map
     * @return The allowed setting values as DTO
     */
    @Named("toAllowedSettingValuesDTOs")
    @IterableMapping(qualifiedByName = "toAllowedSettingValueDTO")
    public abstract List<AllowedSettingValueResponseDto> toAllowedSettingValuesDTOs(Collection<AllowedSettingValue> allowedSettingValues);
}
