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

package io.suricate.monitoring.model.entities;

import io.suricate.monitoring.model.entities.generic.AbstractEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Role entity
 */
@Entity(name = "Role")
@Getter
@Setter
@NoArgsConstructor
public class Role extends AbstractEntity<Long> {
    /**
     * The id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The role name
     */
    @NotNull
    @Size(max = 50)
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * The role description
     */
    @NotNull
    @Size(max = 100)
    @Column(nullable = false)
    private String description;

    /**
     * The list of user related to it
     */
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new LinkedHashSet<>();
}
