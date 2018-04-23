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

package io.suricate.monitoring.service.api;

import io.suricate.monitoring.configuration.security.ConnectedUser;
import io.suricate.monitoring.controllers.api.error.exception.ApiException;
import io.suricate.monitoring.model.entity.project.Project;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.suricate.monitoring.model.enums.AuthenticationMethod;
import io.suricate.monitoring.model.enums.UserRoleEnum;
import io.suricate.monitoring.model.entity.user.Role;
import io.suricate.monitoring.model.entity.user.User;
import io.suricate.monitoring.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service used to manage user
 */
@Service
public class UserService {

    /**
     * Class logger
     * */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    /**
     * The user repository
     */
    private final UserRepository userRepository;
    /**
     * The role service
     */
    private final RoleService roleService;

    /**
     * Constructor
     *
     * @param userRepository The user repository
     * @param roleService The role service
     */
    @Autowired
    public UserService(final UserRepository userRepository, final RoleService roleService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
    }

    /**
     * Register a new user in the database
     *
     * @param user User to register
     * @return The user registered
     */
    public Optional<User> registerNewUserAccount(User user) {
        Optional<Role> role;

        if(userRepository.count() > 0L) {
            role = roleService.getRoleByName(UserRoleEnum.ROLE_USER.name());
        } else {
            role = roleService.getRoleByName(UserRoleEnum.ROLE_ADMIN.name());
        }

        if(!role.isPresent()) {
            LOGGER.debug("Cannot find Role");
            return Optional.empty();
        }

        user.setRoles(Collections.singletonList(role.get()));
        userRepository.save(user);

        return Optional.of(user);
    }

    /**
     * Init a user (LDAP auth mode)
     *
     * @param connectedUser The connected user
     * @return The user
     */
    @Transactional
    public Optional<User> initUser(ConnectedUser connectedUser){

        if (connectedUser == null){
            return Optional.empty();
        }

        // Create user
        User user = new User();
        user.setFirstname(connectedUser.getFirstname());
        user.setLastname(connectedUser.getLastname());
        user.setUsername(connectedUser.getUsername());
        user.setEmail(connectedUser.getMail());
        user.setAuthenticationMethod(AuthenticationMethod.LDAP);

        Optional<Role> role;
        if(userRepository.count() > 0) {
            role = roleService.getRoleByName(UserRoleEnum.ROLE_USER.name());
        } else {
            role = roleService.getRoleByName(UserRoleEnum.ROLE_ADMIN.name());
        }

        if (!role.isPresent()) {
            LOGGER.error("Role {} not available in database", UserRoleEnum.ROLE_USER);
            throw new ApiException(ApiErrorEnum.DATABASE_INIT_ISSUE);
        }

        user.getRoles().add(role.get());
        userRepository.save(user);  // Save user

        return Optional.of(user);
    }

    /**
     * Get every user in database
     *
     * @return The list of users
     */
    public Optional<List<User>> getAll() {
        List<User> users = userRepository.findAll();

        if(users == null || users.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(users);
    }

    /**
     * Get a user by id
     *
     * @param userId The user id
     * @return The user as optional
     */
    public Optional<User> getOne(Long userId) {
        User user = userRepository.findOne(userId);
        if(user == null) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    /**
     * Get a user by username
     *
     * @param username The username to find
     * @return The user as optional
     */
    public Optional<User> getOneByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    /**
     * Get the user id by username
     * @param username The username to find
     * @return The id
     */
    public Long getIdByUsername(String username) {
        return userRepository.getIdByUsername(username);
    }

    /**
     * Search users with the username starting with query
     *
     * @param username The part of username to find
     * @return The list of related users
     */
    public Optional<List<User>> getAllByUsernameStartWith(String username) {
        return userRepository.findByUsernameIgnoreCaseAndStartingWith(username);
    }

    /**
     * Get every user related to a project id
     *
     * @param project The project
     * @return The list of users
     */
    public Optional<List<User>> getAllByProject(Project project) {
        return userRepository.findByProjects_Id(project.getId());
    }
}
