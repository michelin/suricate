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

import io.suricate.monitoring.model.enums.RepositoryTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Describe a repository
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Repository {
    /**
     * The repository id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The repository name
     */
    @Column(unique = true)
    private String name;

    /**
     * The repository url
     */
    @Column
    private String url;

    /**
     * The repository branch to clone
     */
    @Column
    private String branch;

    /**
     * The login to use for the connection to the remote repository
     */
    @Column
    private String login;

    /**
     * The password to use for the connection to the remote repository
     */
    @Column
    private String password;

    /**
     * The path of the repository in case of a local folder
     */
    @Column
    private String localPath;

    /**
     * The type of repository
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RepositoryTypeEnum type;

    /**
     * If the repository is enable or not
     */
    @Column(nullable = false)
    @Type(type = "yes_no")
    private boolean enabled = true;

    /**
     * The list of widgets for this repository
     */
    @OneToMany(mappedBy = "repository")
    private Set<Widget> widgets = new LinkedHashSet<>();
}
