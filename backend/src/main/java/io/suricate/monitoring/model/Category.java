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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.util.List;

/**
 * Project entity
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Indexed
public class Category extends AbstractModel<Long> implements Comparable<Category> {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @Field
    private String name;

    @Column(nullable = false, unique = true)
    private String technicalName;

    @OneToOne(cascade = CascadeType.REMOVE)
    private Asset image;

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<Widget> widgets;

    @Override
    public String getExplicitName(){
        return name;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Widget> getWidgets() {
        return widgets;
    }

    public void setWidgets(List<Widget> widgets) {
        this.widgets = widgets;
    }

    public String getTechnicalName() {
        return technicalName;
    }

    public void setTechnicalName(String technicalName) {
        this.technicalName = technicalName;
    }

    @Override
    public int compareTo(Category o) {
        if (o == null){
            return -1;
        }
        return getExplicitName().compareTo(o.getExplicitName());
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Asset getImage() {
        return image;
    }

    public void setImage(Asset image) {
        this.image = image;
    }
}
