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

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Contains every possible value for a setting
 */
@Entity(name = "allowed_setting_value")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class AllowedSettingValue {

    /**
     * The setting id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The title to display for the user
     */
    @Column(nullable = false)
    private String title;

    /**
     * The value of the entry (used in the code)
     */
    @Column(nullable = false)
    private String value;

    /**
     * True if this setting is the default setting
     */
    @Column(name = "is_default", nullable = false)
    @Type(type = "yes_no")
    private boolean isDefault;

    /**
     * The related setting
     */
    @ManyToOne
    @JoinColumn(name = "setting_id", referencedColumnName = "id", nullable = false)
    private Setting setting;
}
