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

import io.suricate.monitoring.model.entity.setting.AllowedSettingValue;
import io.suricate.monitoring.model.entity.setting.UserSetting;
import io.suricate.monitoring.model.entity.user.User;
import io.suricate.monitoring.repository.UserSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The user setting service
 */
@Service
public class UserSettingService {

    /**
     * Class logger
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(UserSettingService.class);

    /**
     * The setting service
     */
    private final SettingService settingService;

    private final UserSettingRepository userSettingRepository;

    /**
     * Controller
     *
     * @param settingService The setting service
     */
    @Autowired
    public UserSettingService(final SettingService settingService,
                              final UserSettingRepository userSettingRepository) {
        this.settingService = settingService;
        this.userSettingRepository = userSettingRepository;
    }

    /**
     * Create the default list of user settings (in case of new user for example)
     *
     * @param user The user that we have to create settings for
     */
    @Transactional
    public List<UserSetting> createDefaultSettingsForUser(final User user) {
        List<UserSetting> userSettings = new ArrayList<>();

        settingService
            .getAll()
            .ifPresent(settings -> userSettings.addAll(
                settings
                    .stream()
                    .flatMap(setting -> setting.getAllowedSettingValues().stream())
                    .filter(AllowedSettingValue::isDefault)
                    .map(allowedSettingValue -> this.createUserSettingFromAllowedSettingValue(allowedSettingValue, user))
                    .collect(Collectors.toList())
            ));

        this.userSettingRepository.save(userSettings);
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
}
