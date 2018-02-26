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

package io.suricate.monitoring.model.entity.project;


import io.suricate.monitoring.model.entity.AbstractAuditingEntity;
import io.suricate.monitoring.model.entity.user.User;

import javax.persistence.*;
import java.util.List;

/**
 * Project entity
 */
@Entity
public class Project extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String token;

    @OneToMany(mappedBy = "project",cascade = CascadeType.REMOVE)
    private List<ProjectWidget> widgets;

    @Column
    private Integer widgetHeight;

    @Column
    private Integer maxColumn;

    @Lob
    private String cssStyle;

    @ManyToMany
    @JoinTable(name="user_project", joinColumns={@JoinColumn(name="project_id")}, inverseJoinColumns={@JoinColumn(name="user_id")})
    private List<User> users;

    public Project() {}

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

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public List<ProjectWidget> getWidgets() {
        return widgets;
    }
    public void setWidgets(List<ProjectWidget> widgets) {
        this.widgets = widgets;
    }

    public Integer getWidgetHeight() {
        return widgetHeight;
    }
    public void setWidgetHeight(Integer widgetHeight) {
        this.widgetHeight = widgetHeight;
    }

    public Integer getMaxColumn() {
        return maxColumn;
    }
    public void setMaxColumn(Integer maxColumn) {
        this.maxColumn = maxColumn;
    }

    public List<User> getUsers() {
        return users;
    }
    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getCssStyle() {
        return cssStyle;
    }
    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }
}
