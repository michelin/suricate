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

package io.suricate.monitoring.model.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * The configuration entity
 */
@Entity
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(callSuper = false) @ToString
public class Configuration extends AbstractAuditingEntity<String> {

    /**
     * The key of the configuration (used in JS Files)
     */
    @Id
    @Column(name = "config_key",nullable = false, unique = true)
    private String key;

    /**
     * The related value enter by the user
     */
    @Column(name = "config_value", nullable = false)
    private String value;

    /**
     * export
     */
    @Column(name = "config_export")
    private boolean export;

    @Override
    public String getId() {
        return key;
    }
}
