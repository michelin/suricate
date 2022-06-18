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

package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.dto.api.user.UserSettingRequestDto;
import io.suricate.monitoring.model.entities.AllowedSettingValue;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.model.entities.UserSetting;
import io.suricate.monitoring.repositories.UserSettingRepository;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The user setting service
 */
@Service
public class UserSettingService {

    /**
     * The user setting repository
     */
    private final UserSettingRepository userSettingRepository;

    /**
     * The setting service
     */
    private final SettingService settingService;

    /**
     * The allowed setting value service
     */
    private final AllowedSettingValueService allowedSettingValueService;

    /**
     * Controller
     *
     * @param userSettingRepository      The user setting repository
     * @param settingService             The setting service
     * @param allowedSettingValueService The allowed setting service
     */
    @Autowired
    public UserSettingService(final UserSettingRepository userSettingRepository,
                              final SettingService settingService,
                              final AllowedSettingValueService allowedSettingValueService) {
        this.userSettingRepository = userSettingRepository;
        this.settingService = settingService;
        this.allowedSettingValueService = allowedSettingValueService;
    }

    /**
     * Create the default list of user settings (in case of new user for example)
     *
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
                .map(allowedSettingValue -> this.createUserSettingFromAllowedSettingValue(allowedSettingValue, user))
                .collect(Collectors.toList())
        ));

        this.userSettingRepository.saveAll(userSettings);
        this.userSettingRepository.flush();

        return userSettings;
    }

    /**
     * Create a user setting from AllowedSettingValue (constrained value)
     *
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
     *
     * @param userName The username
     * @param settingId The setting ID
     * @return The user setting
     */
    public Optional<UserSetting> getUserSetting(final String userName, final Long settingId) {
        return userSettingRepository.findByUserUsernameAndSettingId(userName, settingId);
    }

    /**
     * Get user settings
     *
     * @param username The username
     * @return The user settings
     */
    public Optional<List<UserSetting>> getUserSettingsByUsername(final String username) {
        return userSettingRepository.findAllByUserUsernameIgnoreCase(username);
    }

    /**
     * Update the settings for a user
     *
     * @param userName              The user name
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
                .getOneById(userSettingRequestDto.getAllowedSettingValueId())
                .ifPresent(userSetting::setSettingValue);
        } else {
            userSetting.setUnconstrainedValue(userSettingRequestDto.getUnconstrainedValue());
        }
        userSettingRepository.save(userSetting);
    }
}
