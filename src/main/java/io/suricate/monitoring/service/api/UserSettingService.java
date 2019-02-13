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

package io.suricate.monitoring.service.api;

import io.suricate.monitoring.model.dto.api.user.UserSettingRequestDto;
import io.suricate.monitoring.model.entity.setting.AllowedSettingValue;
import io.suricate.monitoring.model.entity.setting.UserSetting;
import io.suricate.monitoring.model.entity.user.User;
import io.suricate.monitoring.repository.UserSettingRepository;
import io.suricate.monitoring.utils.exception.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
     * @param userId    The user Id
     * @param settingId The setting id
     * @return The user setting as optional
     */
    public Optional<UserSetting> getUserSetting(final Long userId, final Long settingId) {
        return userSettingRepository.findByUser_IdAndSetting_Id(userId, settingId);
    }

    /**
     * Update the settings for a user
     *
     * @param userId                The user id
     * @param settingId             The setting id
     * @param userSettingRequestDto The new user setting value
     * @return The user settings updated
     */
    public UserSetting updateUserSetting(final Long userId, final Long settingId, final UserSettingRequestDto userSettingRequestDto) {
        Optional<UserSetting> userSettingOptional = getUserSetting(userId, settingId);

        if (!userSettingOptional.isPresent()) {
            throw new ObjectNotFoundException(UserSetting.class, "userId: " + userId + ", settingId: " + settingId);
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

        return userSetting;
    }
}
