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
import io.suricate.monitoring.model.enums.DataTypeEnum;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a param for a widget in database
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class WidgetParam extends AbstractAuditingEntity<Long> {

    /**
     * The widget param id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the param
     */
    @Column(nullable = false)
    private String name;

    /**
     * The description of this params
     */
    @Column(nullable = false)
    private String description;

    /**
     * The default value
     */
    @Column
    private String defaultValue;

    /**
     * The variable type {@link DataTypeEnum}
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DataTypeEnum type;

    /**
     * The regex used for accept the file uploaded (if the type is FILE)
     */
    @Column
    private String acceptFileRegex;

    /**
     * An example of the usage
     */
    @Column
    private String usageExample;

    /**
     * If this param is required or not
     */
    @Column(nullable = false)
    @Type(type = "yes_no")
    private boolean required = true;

    /**
     * The list of possible values (if the type is COMBO or MULTIPLE)
     */
    @OneToMany(mappedBy = "widgetParam", cascade = CascadeType.ALL)
    private List<WidgetParamValue> possibleValuesMap = new ArrayList<>();

    /**
     * The related widget
     */
    @ManyToOne
    @JoinColumn(name = "widget_id")
    private Widget widget;

    /**
     * Add a list of possible values
     *
     * @param possibleValuesMap The list values to add
     */
    public void setPossibleValuesMap(List<WidgetParamValue> possibleValuesMap) {
        this.addPossibleValuesMap(possibleValuesMap);
    }

    /**
     * Add a list of possible values
     *
     * @param possibleValuesMap The list values to add
     */
    public void addPossibleValuesMap(List<WidgetParamValue> possibleValuesMap) {
        possibleValuesMap.forEach(this::addPossibleValueMap);
    }

    /**
     * Add a value into the list
     *
     * @param possibleValueMap The param value to add
     */
    public void addPossibleValueMap(WidgetParamValue possibleValueMap) {
        this.possibleValuesMap.add(possibleValueMap);
        possibleValueMap.setWidgetParam(this);
    }
}
