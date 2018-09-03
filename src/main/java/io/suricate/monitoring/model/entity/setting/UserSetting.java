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

package io.suricate.monitoring.model.entity.setting;

import io.suricate.monitoring.model.entity.user.User;
import lombok.*;

import javax.persistence.*;

/**
 * Class linked the table user and settings
 */
@Entity(name = "user_setting")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class UserSetting {

    /**
     * The user setting id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The related user
     */
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    /**
     * The setting reference
     */
    @OneToOne
    @JoinColumn(name = "setting_id", nullable = false)
    private Setting setting;

    /**
     * The allowed setting value
     */
    @OneToOne
    @JoinColumn(name = "allowed_setting_value_id")
    private AllowedSettingValue settingValue;

    /**
     * The unconstrained value
     */
    @Column(name = "unconstrained_value")
    private String unconstrainedValue;
}
