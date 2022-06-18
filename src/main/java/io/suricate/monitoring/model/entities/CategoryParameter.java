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

package io.suricate.monitoring.model.entities;

import io.suricate.monitoring.model.entities.generic.AbstractAuditingEntity;
import io.suricate.monitoring.model.enums.DataTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * The configuration entity
 */
@Entity
@Table(name = "category_param")
@Getter
@Setter
@NoArgsConstructor
public class CategoryParameter extends AbstractAuditingEntity<String> {
    /**
     * The key of the configuration (used in JS Files)
     */
    @Id
    @Column(name = "config_key", nullable = false, unique = true)
    private String key;

    /**
     * The related value enter by the user
     */
    @Column(name = "config_value")
    private String value;

    /**
     * The description
     */
    @Column
    private String description;

    /**
     * export
     */
    @Column(name = "config_export")
    private boolean export;

    /**
     * The data type of the configuration
     */
    @Column
    @Enumerated(value = EnumType.STRING)
    private DataTypeEnum dataType;

    /**
     * Make a link between category and configurations
     */
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    /**
     * Get the ID of this entity
     *
     * @return The key
     */
    @Override
    public String getId() {
        return key;
    }
}
