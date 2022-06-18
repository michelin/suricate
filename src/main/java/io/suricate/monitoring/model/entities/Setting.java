/*
 *
 *  * Copyright 2012-2021 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.suricate.monitoring.model.entities;

import io.suricate.monitoring.model.enums.DataTypeEnum;
import io.suricate.monitoring.model.enums.SettingType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Setting entity
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Setting {
    /**
     * The setting id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private DataTypeEnum dataType;

    /**
     * The setting type
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SettingType type;

    /**
     * Hold the possible values
     */
    @OneToMany(mappedBy = "setting")
    private Set<AllowedSettingValue> allowedSettingValues = new LinkedHashSet<>();
}
