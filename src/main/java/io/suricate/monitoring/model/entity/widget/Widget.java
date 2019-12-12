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

package io.suricate.monitoring.model.entity.widget;

import io.suricate.monitoring.model.entity.AbstractAuditingEntity;
import io.suricate.monitoring.model.entity.Asset;
import io.suricate.monitoring.model.entity.Library;
import io.suricate.monitoring.model.entity.project.ProjectWidget;
import io.suricate.monitoring.model.enums.WidgetAvailabilityEnum;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Widget entity in database
 * (Retrieve from the widget repo)
 */
@Entity
@Indexed
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class Widget extends AbstractAuditingEntity<Long> {

    /**
     * The widget id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The widget name
     */
    @Column(nullable = false)
    @SortableField
    @Field
    @Boost(3)
    private String name;

    /**
     * The widget description
     */
    @Column(nullable = false)
    @Field
    private String description;

    /**
     * The technical name of the widget
     */
    @Column(unique = true)
    private String technicalName;

    /**
     * The html content of the widget
     */
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String htmlContent;

    /**
     * The css content of the widget
     */
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String cssContent;

    /**
     * The JS of this widget
     */
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String backendJs;

    /**
     * Some information on the usage of the widget
     */
    @Column
    private String info;

    /**
     * The default refresh delay (Nashorn)
     */
    @Column
    private Long delay;

    /**
     * The default timeout (Nashorn)
     */
    @Column
    private Long timeout;

    /**
     * The representation image
     */
    @OneToOne(cascade = CascadeType.REMOVE)
    private Asset image;

    /**
     * The list of instances related to it
     */
    @OneToMany(mappedBy = "widget")
    private List<ProjectWidget> widgetInstances = new ArrayList<>();

    /**
     * The related JS librairie used for displaying it on the clients
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "widget_library", joinColumns = {@JoinColumn(name = "widget_id")}, inverseJoinColumns = {@JoinColumn(name = "library_id")})
    private List<Library> libraries = new ArrayList<>();

    /**
     * The category of this widget
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @IndexedEmbedded(depth = 1)
    private Category category;

    /**
     * The widget availability {@link WidgetAvailabilityEnum}
     */
    @Column
    @Enumerated(EnumType.STRING)
    @Field
    private WidgetAvailabilityEnum widgetAvailability;

    /**
     * The related repository
     */
    @ManyToOne
    @JoinColumn(name = "repository_id")
    private Repository repository;

    /**
     * The list of params for this widget
     */
    @OneToMany(mappedBy = "widget", cascade = CascadeType.ALL)
    private List<WidgetParam> widgetParams = new ArrayList<>();

    /**
     * Widget params setter
     *
     * @param widgetParams The list of params to set
     */
    public void setWidgetParams(List<WidgetParam> widgetParams) {
        this.addWidgetParams(widgetParams);
    }

    /**
     * The list of widget params to add
     *
     * @param widgetParams The list of widget params
     */
    public void addWidgetParams(List<WidgetParam> widgetParams) {
        widgetParams.forEach(this::addWidgetParam);
    }

    /**
     * Add a new param into the list
     *
     * @param widgetParam The param to ass
     */
    public void addWidgetParam(WidgetParam widgetParam) {
        this.widgetParams.add(widgetParam);
        widgetParam.setWidget(this);
    }
}
