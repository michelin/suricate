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

package io.suricate.monitoring.repository;

import io.suricate.monitoring.model.entity.setting.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository used for request UserSettings in database
 */
public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {

    /**
     * Get a setting by userName and setting Id
     *
     * @param userName  The userName
     * @param settingId The setting Id
     * @return The user setting associated
     */
    Optional<UserSetting> findByUser_UsernameAndSetting_Id(String userName, Long settingId);
}
