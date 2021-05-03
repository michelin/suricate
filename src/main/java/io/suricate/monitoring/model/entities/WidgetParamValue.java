/*
 *
 *  * Copyright 2012-2018 the original author or authors.
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

import io.suricate.monitoring.model.entities.generic.AbstractAuditingEntity;
import lombok.*;

import javax.persistence.*;

/**
 * The widget param value entity in database
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class WidgetParamValue extends AbstractAuditingEntity<Long> {

    /**
     * The id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The param key used in the JS File of the widget
     */
    @Column(nullable = false)
    private String jsKey;

    /**
     * The related value (for display)
     */
    @Column(nullable = false)
    private String value;

    /**
     * The related widget param
     */
    @ManyToOne
    @JoinColumn(name = "widget_param_id")
    private WidgetParam widgetParam;
}
