/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.michelin.suricate.model.entity;

import com.michelin.suricate.model.entity.generic.AbstractAuditingEntity;
import com.michelin.suricate.model.enumeration.DataTypeEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.type.YesNoConverter;

/**
 * Widget param entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class WidgetParam extends AbstractAuditingEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column
    private String defaultValue;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DataTypeEnum type;

    @Column
    private String acceptFileRegex;

    @Column
    private String usageExample;

    @Column
    private String usageTooltip;

    @Column(nullable = false)
    @Convert(converter = YesNoConverter.class)
    private boolean required = true;

    @ToString.Exclude
    @OneToMany(mappedBy = "widgetParam", cascade = CascadeType.ALL)
    private Set<WidgetParamValue> possibleValuesMap = new LinkedHashSet<>();

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "widget_id")
    private Widget widget;

    public void setPossibleValuesMap(Collection<WidgetParamValue> possibleValuesMap) {
        addPossibleValuesMap(possibleValuesMap);
    }

    public void addPossibleValuesMap(Collection<WidgetParamValue> possibleValuesMap) {
        possibleValuesMap.forEach(this::addPossibleValueMap);
    }

    public void addPossibleValueMap(WidgetParamValue possibleValueMap) {
        this.possibleValuesMap.add(possibleValueMap);
        possibleValueMap.setWidgetParam(this);
    }

    /**
     * Hashcode method.
     * Do not use lombok @EqualsAndHashCode method as it calls super method
     * then call the self-defined child Hashcode method
     *
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Equals method.
     * Do not use lombok @EqualsAndHashCode method as it calls super method
     * then call the self-defined child Equals method
     *
     * @param other The other object to compare
     * @return true if equals, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        return super.equals(other);
    }
}
