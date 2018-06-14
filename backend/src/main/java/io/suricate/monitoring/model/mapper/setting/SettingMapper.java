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

import io.suricate.monitoring.model.dto.setting.SettingDto;
import io.suricate.monitoring.model.entity.setting.Setting;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for Setting class
 */
@Component
@Mapper(
    componentModel = "spring",
    uses = {
        AllowedSettingValueMapper.class
    }
)
public abstract class SettingMapper {

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Transform a setting into a SettingDto
     *
     * @param setting The setting to tranform
     * @return The related DTO
     */
    @Named("toSettingDtoDefault")
    @Mappings({
        @Mapping(target = "allowedSettingValues", qualifiedByName = "toAllowedSettingValueDtosWithoutSetting")
    })
    public abstract SettingDto toSettingDtoDefault(Setting setting);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Transform a list of settings into a list of settings dto
     *
     * @param settings The list of settings to transform
     * @return The related list of dto's
     */
    @Named("toSettingDtosDefault")
    @IterableMapping(qualifiedByName = "toSettingDtoDefault")
    public abstract List<SettingDto> toSettingDtosDefault(List<Setting> settings);

}
