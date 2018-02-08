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

package io.suricate.monitoring.service;

import io.suricate.monitoring.config.security.token.TokenService;
import io.suricate.monitoring.model.Project;
import io.suricate.monitoring.model.dto.user.UserDto;
import io.suricate.monitoring.model.user.User;
import io.suricate.monitoring.repository.ProjectRepository;
import io.suricate.monitoring.repository.RoleRepository;
import io.suricate.monitoring.repository.UserRepository;
import io.suricate.monitoring.service.ldap.LdapService;
import io.suricate.monitoring.utils.ApplicationConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service used to manage user
 */
@Service
public class UserService {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private LdapService ldapService;

    @Autowired
    private TokenService tokenService;


    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> new UserDto(user, ldapService.getUserDetails(user.getUsername()))).collect(Collectors.toList());
    }

    public UserDto getOne(Long userId) {
        User user = userRepository.findOne(userId);
        return new UserDto(user, ldapService.getUserDetails(user.getUsername()));
    }

    /**
     * Method used to remove user form project
     * @param projectId the current project id
     * @param userId the user id
     */
    @Transactional
    public void removeUser(Long projectId, Long userId){
        Project project = projectRepository.findOne(projectId);
        if (project != null) {
            project.getUsers().removeIf(user -> user.getId().equals(userId));
            projectRepository.save(project);
        } else {
            LOGGER.error("Project not found for id {}", projectId);
        }
    }


    /**
     * Method used to get all used linked to the project with there id
     * @param projectId the project id
     * @return the list of user
     */
    public List<UserDto> getUserForProject(Long projectId) {
        List<User> user = userRepository.findByProjects_Id(projectId);
        return user.stream().map(u -> new UserDto(u, ldapService.getUserDetails(u.getUsername()))).collect(Collectors.toList());
    }

    /**
     * Method used to search a user
     * @param query the query to search users
     * @return the list of user found
     */
    public List<UserDto> searchUser(String query) {
        return ldapService.searchUser(query).stream().map(UserDto::new).collect(Collectors.toList());
    }

    /**
     * Method used to add a user to the project Id
     * @param username the username to Add
     * @param projectId the project Id
     */
    public void addUserToProject(String username, Long projectId){
        Project project = projectRepository.findOne(projectId);
        User user = userRepository.findByUsername(username);
        if (user == null){
            user = new User();
            user.setRoles(Collections.singletonList(roleRepository.findByName(ApplicationConstant.ROLE_USER)));
            user.setUsername(username);
            user.setToken(tokenService.generateToken());
            userRepository.save(user);
        }
        // Check not already defined user
        if (project.getUsers().stream().noneMatch(u -> u.getUsername().equalsIgnoreCase(username))){
            project.getUsers().add(user);
            projectRepository.save(project);
        }
    }
}
