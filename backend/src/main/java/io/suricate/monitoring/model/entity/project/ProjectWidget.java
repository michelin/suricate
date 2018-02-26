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
import io.suricate.monitoring.model.entity.widget.Widget;
import io.suricate.monitoring.model.enums.WidgetState;

import javax.persistence.*;
import java.util.Date;

/**
 * Project_widget entity
 */
@Entity(name = "ProjectWidget")
public class ProjectWidget extends AbstractAuditingEntity<Long> {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    @Lob
    private String data;

    @Column
    private int row;

    @Column
    private int col;

    @Column
    private int width;

    @Column
    private int height;

    @Lob
    private String customStyle;

    @Lob
    private String backendConfig;

    @Lob
    private String log;

    @Column
    private Date lastExecutionDate;

    @Column
    private Date lastSuccessDate;

    @Column
    @Enumerated(EnumType.STRING)
    private WidgetState state;

    @ManyToOne
    @PrimaryKeyJoinColumn(name="projectId",referencedColumnName = "ID")
    private Project project;

    @ManyToOne
    @PrimaryKeyJoinColumn(name = "widgetId" ,referencedColumnName = "ID")
    private Widget widget;

    public ProjectWidget() {}

    @Override
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }

    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        this.row = row;
    }

    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }

    public Project getProject() {
        return project;
    }
    public void setProject(Project project) {
        this.project = project;
    }

    public Widget getWidget() {
        return widget;
    }
    public void setWidget(Widget widget) {
        this.widget = widget;
    }

    public String getCustomStyle() {
        return customStyle;
    }
    public void setCustomStyle(String customStyle) {
        this.customStyle = customStyle;
    }

    public String getBackendConfig() {
        return backendConfig;
    }
    public void setBackendConfig(String backendConfig) {
        this.backendConfig = backendConfig;
    }

    public int getCol() {
        return col;
    }
    public void setCol(int col) {
        this.col = col;
    }

    public String getLog() {
        return log;
    }
    public void setLog(String log) {
        this.log = log;
    }

    public Date getLastExecutionDate() {
        return lastExecutionDate;
    }
    public void setLastExecutionDate(Date lastExecutionDate) {
        this.lastExecutionDate = lastExecutionDate;
    }

    public Date getLastSuccessDate() {
        return lastSuccessDate;
    }
    public void setLastSuccessDate(Date lastSuccessDate) {
        this.lastSuccessDate = lastSuccessDate;
    }

    public WidgetState getState() {
        return state;
    }
    public void setState(WidgetState state) {
        this.state = state;
    }
}
