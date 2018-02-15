/*
 *  /*
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
 *
 */

package io.suricate.monitoring.model;

import io.suricate.monitoring.model.enums.WidgetVariableType;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class WidgetParam extends AbstractModel<Long> {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column
    private String defaultValue;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private WidgetVariableType type;

    @Column
    private String acceptFileRegex;

    @Column
    private String usageExample;

    @Column(nullable = false)
    @Type(type = "yes_no")
    private boolean required = true;

    @OneToMany(mappedBy = "widgetParam", cascade = CascadeType.ALL)
    private List<WidgetParamValue> possibleValuesMap = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "widget_id")
    private Widget widget;



    public WidgetParam() {}


    @Override
    public String getExplicitName() {
        return this.name;
    }

    @Override
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public WidgetVariableType getType() {
        return type;
    }
    public void setType(WidgetVariableType type) {
        this.type = type;
    }

    public String getAcceptFileRegex() {
        return acceptFileRegex;
    }
    public void setAcceptFileRegex(String acceptFileRegex) {
        this.acceptFileRegex = acceptFileRegex;
    }

    public String getUsageExample() {
        return usageExample;
    }
    public void setUsageExample(String usageExample) {
        this.usageExample = usageExample;
    }

    public boolean isRequired() {
        return required;
    }
    public void setRequired(boolean required) {
        this.required = required;
    }

    public Widget getWidget() {
        return widget;
    }
    public void setWidget(Widget widget) {
        this.widget = widget;
    }


    public List<WidgetParamValue> getPossibleValuesMap() {
        return possibleValuesMap;
    }
    public void setPossibleValuesMap(List<WidgetParamValue> possibleValuesMap) {
        this.addPossibleValuesMap(possibleValuesMap);
    }
    public void addPossibleValuesMap(List<WidgetParamValue> possibleValuesMap) {
        possibleValuesMap.forEach( possibleValueMap -> {
            this.possibleValuesMap.add(possibleValueMap);
            possibleValueMap.setWidgetParam(this);
        });
    }
}
