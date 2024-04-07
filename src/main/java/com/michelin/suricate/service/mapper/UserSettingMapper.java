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

import com.michelin.suricate.model.dto.api.user.UserSettingResponseDto;
import com.michelin.suricate.model.entity.UserSetting;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * User setting mapper.
 */
@Mapper(
    componentModel = "spring",
    uses = {
        SettingMapper.class,
        AllowedSettingValueMapper.class
    },
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserSettingMapper {

    /**
     * Map a user setting into a user setting DTO.
     *
     * @param userSetting The user setting to map
     * @return The user setting DTO
     */
    @Named("toUserSettingDto")
    @Mapping(target = "userId", source = "userSetting.user.id")
    @Mapping(target = "setting", qualifiedByName = "toSettingDto")
    @Mapping(target = "settingValue", qualifiedByName = "toAllowedSettingValueDto")
    public abstract UserSettingResponseDto toUserSettingDto(UserSetting userSetting);

    /**
     * Map a list of user settings into a list of user settings DTOs.
     *
     * @param userSettings The list of user settings to map
     * @return The list of user settings as DTO
     */
    @Named("toUserSettingsDtos")
    @IterableMapping(qualifiedByName = "toUserSettingDto")
    public abstract List<UserSettingResponseDto> toUserSettingsDtos(List<UserSetting> userSettings);
}
