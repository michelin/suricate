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

package com.michelin.suricate.model.entities;

import com.michelin.suricate.model.entities.generic.AbstractAuditingEntity;
import com.michelin.suricate.model.enums.DataTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

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
    @Type(type = "yes_no")
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

    @Override
    public int hashCode() { return super.hashCode(); }

    @Override
    public boolean equals(Object other) { return super.equals(other); }
}
