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

import io.suricate.monitoring.model.entities.generic.AbstractEntity;
import io.suricate.monitoring.model.enums.AuthenticationMethod;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The user entity in database
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class User extends AbstractEntity<Long> {

    /**
     * The user id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The username (login) of the user
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * The encrypted password of the user
     */
    @Column
    private String password;

    /**
     * The authentication method {@link AuthenticationMethod}
     */
    @Enumerated(value = EnumType.STRING)
    @Column(name = "auth_mode", nullable = false, length = 20)
    private AuthenticationMethod authenticationMethod;

    /**
     * The fistname of the user
     */
    @Column
    private String firstname;

    /**
     * The lastname of the user
     */
    @Column
    private String lastname;

    /**
     * The mail of the user
     */
    @Column(unique = true)
    private String email;

    /**
     * The list of roles
     */
    @ManyToMany
    @JoinTable(name = "user_role", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private List<Role> roles = new ArrayList<>();

    /**
     * The projects of the user (as creator or guest)
     */
    @ManyToMany(mappedBy = "users")
    private List<Project> projects = new ArrayList<>();

    /**
     * The list of user settings
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<UserSetting> userSettings = new ArrayList<>();
}
