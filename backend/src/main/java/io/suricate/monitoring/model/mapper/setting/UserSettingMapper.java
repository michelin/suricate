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

import io.suricate.monitoring.model.dto.setting.UserSettingDto;
import io.suricate.monitoring.model.entity.setting.UserSetting;
import io.suricate.monitoring.model.mapper.role.UserMapper;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for UserSetting class
 */
@Component
@Mapper(
    componentModel = "spring",
    uses = {
        UserMapper.class,
        SettingMapper.class,
        AllowedSettingValueMapper.class
    }
)
public abstract class UserSettingMapper {

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Transform a user setting into a user setting dto without user
     *
     * @param userSetting The user setting to transform
     * @return The related DTO
     */
    @Named("toUserSettingDtoWithoutUser")
    @Mappings({
        @Mapping(target = "user", ignore = true),
        @Mapping(target = "setting", qualifiedByName = "toSettingDtoDefault"),
        @Mapping(target = "settingValue", qualifiedByName = "toAllowedSettingValueDtoWithoutSetting")
    })
    public abstract UserSettingDto toUserSettingDtoWithoutUser(UserSetting userSetting);


    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Transform a list of userSetting into a list of userSettingsDto
     *
     * @param userSettings The user settings to transform
     * @return The related list of dto's
     */
    @Named("toUserSettingDtosWithoutUser")
    @IterableMapping(qualifiedByName = "toUserSettingDtoWithoutUser")
    public abstract List<UserSettingDto> toUserSettingDtosWithoutUser(List<UserSetting> userSettings);
}
