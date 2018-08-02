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

import io.suricate.monitoring.model.enums.SettingDataType;
import io.suricate.monitoring.model.enums.SettingType;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Constains every setting to display
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Setting {
    /**
     * The setting id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The setting name/description
     */
    @Column(nullable = false)
    private String description;

    /**
     * Tell if the settings have constrained values
     */
    @Column(nullable = false)
    @Type(type = "yes_no")
    private boolean constrained;

    /**
     * The setting data type
     */
    @Column(nullable = false, name = "data_type")
    @Enumerated(EnumType.STRING)
    private SettingDataType dataType;

    /**
     * The setting type
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SettingType type;

    /**
     * Hold the possible values (if we have a select setting for example)
     */
    @OneToMany(mappedBy = "setting")
    private List<AllowedSettingValue> allowedSettingValues = new ArrayList<>();
}
