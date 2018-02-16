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

package io.suricate.monitoring.model.entity;

import javax.persistence.*;

@Entity
public class WidgetParamValue extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String jsKey;

    @Column(nullable = false)
    private String value;

    @ManyToOne
    @JoinColumn(name = "widget_param_id")
    private WidgetParam widgetParam;


    public WidgetParamValue() {}


    @Override
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getJsKey() {
        return jsKey;
    }
    public void setJsKey(String jsKey) {
        this.jsKey = jsKey;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public WidgetParam getWidgetParam() {
        return widgetParam;
    }
    public void setWidgetParam(WidgetParam widgetParam) {
        this.widgetParam = widgetParam;
    }
}
