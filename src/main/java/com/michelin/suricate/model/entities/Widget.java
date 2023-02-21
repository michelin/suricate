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
import com.michelin.suricate.model.enums.WidgetAvailabilityEnum;
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
public class Widget extends AbstractAuditingEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(unique = true)
    private String technicalName;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String htmlContent;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String cssContent;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String backendJs;

    @Column
    private String info;

    @Column
    private Long delay;

    @Column
    private Long timeout;

    @Column
    @Enumerated(EnumType.STRING)
    private WidgetAvailabilityEnum widgetAvailability;

    @OneToOne(cascade = CascadeType.REMOVE)
    private Asset image;

    @ToString.Exclude
    @OneToMany(mappedBy = "widget")
    private Set<ProjectWidget> widgetInstances = new LinkedHashSet<>();

    @ToString.Exclude
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "widget_library", joinColumns = {@JoinColumn(name = "widget_id")}, inverseJoinColumns = {@JoinColumn(name = "library_id")})
    private Set<Library> libraries = new LinkedHashSet<>();

    @ToString.Exclude
    @ManyToOne
    private Category category;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "repository_id")
    private Repository repository;

    @ToString.Exclude
    @OneToMany(mappedBy = "widget", cascade = CascadeType.ALL)
    @OrderBy("id ASC")
    private Set<WidgetParam> widgetParams = new LinkedHashSet<>();

    public void setWidgetParams(Collection<WidgetParam> widgetParams) {
        addWidgetParams(widgetParams);
    }

    public void addWidgetParams(Collection<WidgetParam> widgetParams) {
        widgetParams.forEach(this::addWidgetParam);
    }

    public void addWidgetParam(WidgetParam widgetParam) {
        widgetParams.add(widgetParam);
        widgetParam.setWidget(this);
    }

    @Override
    public int hashCode() { return super.hashCode(); }

    @Override
    public boolean equals(Object other) { return super.equals(other); }
}
