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

package io.suricate.monitoring.model.dto.project;

import io.suricate.monitoring.model.dto.user.UserDto;
import io.suricate.monitoring.model.dto.widget.WidgetResponse;

import java.util.ArrayList;
import java.util.List;

public class ProjectResponse {

    private Long id;
    private String name;
    private String token;
    private List<WidgetResponse> widgets = new ArrayList<>();
    private Integer widgetHeight;
    private Integer maxColumn;
    private String cssStyle;
    private List<String> librariesToken = new ArrayList<>();
    private List<UserDto> users = new ArrayList<>();

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

    public List<WidgetResponse> getWidgets() {
        return widgets;
    }

    public void setWidgets(List<WidgetResponse> widgets) {
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

    public String getCssStyle() {
        return cssStyle;
    }

    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    public List<String> getLibrariesToken() {
        return librariesToken;
    }

    public void setLibrariesToken(List<String> librariesToken) {
        this.librariesToken = librariesToken;
    }

    public List<UserDto> getUsers() {
        if(users == null) {
            users = new ArrayList<>();
        }
        return users;
    }

    public void setUsers(List<UserDto> users) {
        this.users = users;
    }
}
