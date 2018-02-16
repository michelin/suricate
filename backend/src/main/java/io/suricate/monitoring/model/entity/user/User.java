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

package io.suricate.monitoring.model.entity.user;

import io.suricate.monitoring.model.entity.AbstractEntity;
import io.suricate.monitoring.model.entity.project.Project;
import io.suricate.monitoring.model.enums.AuthenticationMethod;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String password;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "auth_mode", nullable = false, length = 20)
    private AuthenticationMethod authenticationMethod;

    @Column
    private String firstname;

    @Column
    private String lastname;

    @Column(unique = true)
    private String email;

    @ManyToMany
    @JoinTable(name="user_role", joinColumns={@JoinColumn(name="user_id")}, inverseJoinColumns={@JoinColumn(name="role_id")})
    private List<Role> roles;

    @ManyToMany(mappedBy = "users")
    private List<Project> projects;

    @Column(nullable = false, length = 500)
    private String token;


    public User() {}


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public AuthenticationMethod getAuthenticationMethod() {
        return authenticationMethod;
    }
    public void setAuthenticationMethod(AuthenticationMethod authenticationMethod) {
        this.authenticationMethod = authenticationMethod;
    }

    public List<Role> getRoles() {
        if(roles == null) {
            this.roles = new ArrayList<>();
        }
        return roles;
    }
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Project> getProjects() {
        return projects;
    }
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public String getFirstname() {
        return firstname;
    }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
