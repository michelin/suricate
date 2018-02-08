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

import javax.persistence.*;
import java.util.List;

/**
 * Library entity
 */
@Entity
public class Library extends AbstractModel<Long> implements Comparable<Library> {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String technicalName;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.DETACH})
    private Asset asset;

    @ManyToMany(mappedBy = "libraries")
    private List<Widget> widgets;

    public Library() {
        // empty constructor
    }

    public Library(String technicalName) {
        this.technicalName = technicalName;
    }

    @Override
    public String getExplicitName(){
        return technicalName;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getTechnicalName() {
        return technicalName;
    }

    public void setTechnicalName(String technicalName) {
        this.technicalName = technicalName;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    @Override
    public int compareTo(Library o) {
        if (o == null){
            return -1;
        }
        return getExplicitName().compareTo(o.getExplicitName());
    }

    public void setId(Long id) {
        this.id = id;
    }



}
