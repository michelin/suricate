/*
 * Copyright 2012-2021 the original author or authors.
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

package com.michelin.suricate.model.entities;

import com.michelin.suricate.model.entities.generic.AbstractAuditingEntity;
import lombok.*;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class Library extends AbstractAuditingEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String technicalName;

    @OneToOne(cascade = {CascadeType.REMOVE, CascadeType.DETACH})
    private Asset asset;

    @ToString.Exclude
    @ManyToMany(mappedBy = "libraries")
    private Set<Widget> widgets = new LinkedHashSet<>();

    public Library(String technicalName) {
        this.technicalName = technicalName;
    }

    @Override
    public int hashCode() { return super.hashCode(); }

    @Override
    public boolean equals(Object other) { return super.equals(other); }
}
