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

package com.michelin.suricate.services.api;

import com.michelin.suricate.model.dto.api.user.UserSettingRequestDto;
import com.michelin.suricate.model.entities.AllowedSettingValue;
import com.michelin.suricate.repositories.UserSettingRepository;
import com.michelin.suricate.model.entities.User;
import com.michelin.suricate.model.entities.UserSetting;
import com.michelin.suricate.utils.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserSettingService {
    @Autowired
    private UserSettingRepository userSettingRepository;

    @Autowired
    private SettingService settingService;

    @Autowired
    private AllowedSettingValueService allowedSettingValueService;

    /**
     * Create the default list of user settings (in case of new user for example)
     * @param user The user that we have to create settings for
     */
    @Transactional
    public List<UserSetting> createDefaultSettingsForUser(final User user) {
        List<UserSetting> userSettings = new ArrayList<>();

        settingService.getAll().ifPresent(settings -> userSettings.addAll(
            settings
                .stream()
                .flatMap(setting -> setting.getAllowedSettingValues().stream())
                .filter(AllowedSettingValue::isDefault)
                .map(allowedSettingValue -> createUserSettingFromAllowedSettingValue(allowedSettingValue, user))
                .collect(Collectors.toList())
        ));

        userSettingRepository.saveAll(userSettings);
        userSettingRepository.flush();

        return userSettings;
    }

    /**
     * Create a user setting from AllowedSettingValue (constrained value)
     * @param allowedSettingValue The allowed setting value
     * @return The related user setting
     */
    public UserSetting createUserSettingFromAllowedSettingValue(final AllowedSettingValue allowedSettingValue, final User user) {
        UserSetting userSetting = new UserSetting();

        userSetting.setUser(user);
        userSetting.setSetting(allowedSettingValue.getSetting());
        userSetting.setSettingValue(allowedSettingValue);

        return userSetting;
    }

    /**
     * Get a user setting
     * @param userName The username
     * @param settingId The setting ID
     * @return The user setting
     */
    public Optional<UserSetting> getUserSetting(final String userName, final Long settingId) {
        return userSettingRepository.findByUserUsernameAndSettingId(userName, settingId);
    }

    /**
     * Get user settings
     * @param username The username
     * @return The user settings
     */
    public Optional<List<UserSetting>> getUserSettingsByUsername(final String username) {
        return userSettingRepository.findAllByUserUsernameIgnoreCase(username);
    }

    /**
     * Update the settings for a user
     * @param userName              The username
     * @param settingId             The setting id
     * @param userSettingRequestDto The new user setting value
     */
    public void updateUserSetting(final String userName, final Long settingId, final UserSettingRequestDto userSettingRequestDto) {
        Optional<UserSetting> userSettingOptional = getUserSetting(userName, settingId);

        if (!userSettingOptional.isPresent()) {
            throw new ObjectNotFoundException(UserSetting.class, "user: " + userName + ", settingId: " + settingId);
        }

        UserSetting userSetting = userSettingOptional.get();
        if (userSetting.getSetting().isConstrained()) {
            allowedSettingValueService
                .findById(userSettingRequestDto.getAllowedSettingValueId())
                .ifPresent(userSetting::setSettingValue);
        } else {
            userSetting.setUnconstrainedValue(userSettingRequestDto.getUnconstrainedValue());
        }
        userSettingRepository.save(userSetting);
    }
}
