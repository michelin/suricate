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

package io.suricate.monitoring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Project entity
 */
@Entity
@Indexed
public class Widget extends AbstractModel<Long> {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @SortableField
    @Field @Boost(3)
    private String name;

    @Column(nullable = false)
    @Field
    private String description;

    @Column(unique = true)
    private String technicalName;

    @Lob
    private String htmlContent;

    @Lob
    private String cssContent;

    @Lob
    private String backendJs;

    @Column
    private String info;

    @Column
    private Long delay;

    @Column
    private Long timeout;

    @OneToOne(cascade = CascadeType.REMOVE)
    private Asset image;

    @JsonIgnore
    @OneToMany(mappedBy = "widget")
    private List<ProjectWidget> widgetInstances;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name="widget_library", joinColumns={@JoinColumn(name="widget_id")}, inverseJoinColumns={@JoinColumn(name="library_id")})
    private List<Library> libraries;

    @ManyToOne(fetch=FetchType.LAZY)
    @IndexedEmbedded(depth = 1)
    private Category category;

    @Column
    @Enumerated(EnumType.STRING)
    @Field
    private WidgetAvailability widgetAvailability;

    @OneToMany(mappedBy = "widget", cascade = CascadeType.ALL)
    private List<WidgetParam> widgetParams = new ArrayList<>();

    public String getHtmlContent() {
        return htmlContent;
    }
    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String getCssContent() {
        return cssContent;
    }
    public void setCssContent(String cssContent) {
        this.cssContent = cssContent;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getTechnicalName() {
        return technicalName;
    }
    public void setTechnicalName(String technicalName) {
        this.technicalName = technicalName;
    }

    @Override
    public String getExplicitName(){
        return name;
    }

    @Override
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getBackendJs() {
        return backendJs;
    }
    public void setBackendJs(String backendJs) {
        this.backendJs = backendJs;
    }

    public Long getDelay() {
        return delay;
    }
    public void setDelay(Long delay) {
        this.delay = delay;
    }

    public Asset getImage() {
        return image;
    }
    public void setImage(Asset image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }

    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }

    public List<Library> getLibraries() {
        return libraries;
    }
    public void setLibraries(List<Library> libraries) {
        this.libraries = libraries;
    }

    public WidgetAvailability getWidgetAvailability() {
        return widgetAvailability;
    }
    public void setWidgetAvailability(WidgetAvailability widgetAvailability) {
        this.widgetAvailability = widgetAvailability;
    }

    public Long getTimeout() {
        return timeout;
    }
    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }



    public List<WidgetParam> getWidgetParams() {
        return widgetParams;
    }
    public void setWidgetParams(List<WidgetParam> widgetParams) {
        this.addWidgetParams(widgetParams);
    }
    public void addWidgetParams(List<WidgetParam> widgetParams) {
        widgetParams.forEach( widgetParam -> {
            this.widgetParams.add(widgetParam);
            widgetParam.setWidget(this);
        });
    }
}
