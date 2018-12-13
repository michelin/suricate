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

package io.suricate.monitoring.service.mapper;

import io.suricate.monitoring.model.dto.api.user.UserSettingResponseDto;
import io.suricate.monitoring.model.entity.setting.UserSetting;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for UserSetting class
 */
@Component
@Mapper(
    componentModel = "spring",
    uses = {
        AllowedSettingValueMapper.class
    }
)
public abstract class UserSettingMapper {

    /* ************************* TO DTO ********************************************** */

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Transform a user setting into a user setting dto without user
     *
     * @param userSetting The user setting to transform
     * @return The related DTO
     */
    @Named("toUserSettingDtoDefault")
    @Mapping(target = "userId", source = "userSetting.user.id")
    @Mapping(target = "settingId", source = "userSetting.setting.id")
    @Mapping(target = "settingValue", qualifiedByName = "toAllowedSettingValueDtoDefault")
    public abstract UserSettingResponseDto toUserSettingDtoDefault(UserSetting userSetting);


    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Transform a list of userSetting into a list of userSettingsDto
     *
     * @param userSettings The user settings to transform
     * @return The related list of dto's
     */
    @Named("toUserSettingDtosDefault")
    @IterableMapping(qualifiedByName = "toUserSettingDtoDefault")
    public abstract List<UserSettingResponseDto> toUserSettingDtosDefault(List<UserSetting> userSettings);
}
