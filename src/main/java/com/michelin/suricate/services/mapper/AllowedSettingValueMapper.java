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
import java.util.Collection;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

/**
 * Allowed setting value mapper.
 */
@Mapper(componentModel = "spring")
public abstract class AllowedSettingValueMapper {
    /**
     * Map an allowed setting value into a DTO.
     *
     * @param allowedSettingValue The allowed setting value to map
     * @return The allowed setting value as DTO
     */
    @Named("toAllowedSettingValueDto")
    public abstract AllowedSettingValueResponseDto toAllowedSettingValueDto(AllowedSettingValue allowedSettingValue);

    /**
     * Map a list of allowed setting values into a list of DTO.
     *
     * @param allowedSettingValues The list of allowed setting values to map
     * @return The allowed setting values as DTO
     */
    @Named("toAllowedSettingValuesDtos")
    @IterableMapping(qualifiedByName = "toAllowedSettingValueDto")
    public abstract List<AllowedSettingValueResponseDto> toAllowedSettingValuesDtos(
        Collection<AllowedSettingValue> allowedSettingValues);
}
