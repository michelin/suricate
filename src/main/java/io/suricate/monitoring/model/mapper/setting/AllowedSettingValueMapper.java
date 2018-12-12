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

package io.suricate.monitoring.model.mapper.setting;

import io.suricate.monitoring.model.dto.api.setting.AllowedSettingValueResponseDto;
import io.suricate.monitoring.model.entity.setting.AllowedSettingValue;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for AllowedSettingValue class
 */
@Component
@Mapper(componentModel = "spring")
public abstract class AllowedSettingValueMapper {

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Transform an allowedSettingValue into an AllowedSettingValueResponseDto
     *
     * @param allowedSettingValue The setting value to transform
     * @return The related dto
     */
    @Named("toAllowedSettingValueDtoDefault")
    public abstract AllowedSettingValueResponseDto toAllowedSettingValueDtoDefault(AllowedSettingValue allowedSettingValue);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Transform a list of AllowedSettingValue into a list of AllowedSettingValueResponseDto
     *
     * @param allowedSettingValues The list to transform
     * @return The related list of dtos
     */
    @Named("toAllowedSettingValueDtosDefault")
    @IterableMapping(qualifiedByName = "toAllowedSettingValueDtoDefault")
    public abstract List<AllowedSettingValueResponseDto> toAllowedSettingValueDtosDefault(List<AllowedSettingValue> allowedSettingValues);

}
