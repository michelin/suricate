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
import io.suricate.monitoring.model.enums.WidgetStateEnum;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

/**
 * Project_widget entity
 */
@Entity(name = "ProjectWidget")
@Getter
@Setter
@NoArgsConstructor
public class ProjectWidget extends AbstractAuditingEntity<Long> {

    /**
     * The project widget id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The data of the widget (result of nashorn execution)
     */
    @Column
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String data;

    /**
     * Row position in the grid
     */
    @Column
    private int gridRow;

    /**
     * Column position in the grid
     */
    @Column
    private int gridColumn;

    /**
     * The number of rows taken by the widget
     */
    @Column
    private int width;

    /**
     * The number of columns taken by the widget
     */
    @Column
    private int height;

    /**
     * The css style added by the user
     */
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String customStyle;

    /**
     * The configuration of the widget (Result of the widget params)
     */
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String backendConfig;

    /**
     * The nashorn execution log
     */
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String log;

    /**
     * The last execution date
     */
    @Column
    private Date lastExecutionDate;

    /**
     * The last success execution date
     */
    @Column
    private Date lastSuccessDate;
    
    /**
     * The widget state {@link WidgetStateEnum}
     */
    @Column
    @Enumerated(EnumType.STRING)
    private WidgetStateEnum state;

    /**
     * The related project
     */
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "projectId", referencedColumnName = "ID")
    private Project project;

    /**
     * The related widget
     */
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "widgetId", referencedColumnName = "ID")
    private Widget widget;

    /**
     * The related widget
     */
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "widgetId", referencedColumnName = "ID")
    private RotatingScreen rotatingScreen;
}
