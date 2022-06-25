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

package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.entities.Role;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.model.enums.AuthenticationProvider;
import io.suricate.monitoring.model.enums.UserRoleEnum;
import io.suricate.monitoring.repositories.UserRepository;
import io.suricate.monitoring.services.mapper.UserMapper;
import io.suricate.monitoring.services.specifications.UserSearchSpecification;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service used to manage user
 */
@Service
public class UserService {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    /**
     * The user repository
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * The user mapper
     */
    @Autowired
    private UserMapper userMapper;

    /**
     * The role service
     */
    @Autowired
    private RoleService roleService;

    /**
     * The project service
     */
    @Autowired
    private ProjectService projectService;

    /**
     * The user setting service
     */
    @Autowired
    private UserSettingService userSettingService;

    /**
     * Register a new user in database authentication mode
     * @param user User to register
     * @return The user registered
     */
    public User create(User user) {
        UserRoleEnum roleEnum = userRepository.count() > 0 ? UserRoleEnum.ROLE_USER : UserRoleEnum.ROLE_ADMIN;
        Optional<Role> role = roleService.getRoleByName(roleEnum.name());
        if (!role.isPresent()) {
            LOGGER.error("Role {} not available in database", roleEnum);
            throw new ObjectNotFoundException(Role.class, roleEnum);
        }

        user.setRoles(Collections.singleton(role.get()));
        userRepository.save(user);

        user.getUserSettings().addAll(userSettingService.createDefaultSettingsForUser(user));

        return user;
    }

    /**
     * Register a new user in ldap/oauth2 authentication mode
     * @param username The username
     * @param firstname The user firstname
     * @param lastname The user lastname
     * @param email The user email
     * @param avatarUrl The user avatar URL
     * @param authenticationMethod The ID provider used
     * @return The registered user
     */
    @Transactional
    public User registerUser(String username, String firstname, String lastname, String email, String avatarUrl,
                             AuthenticationProvider authenticationMethod) {
        Optional<User> optionalUser = getOneByEmail(email);

        if (!optionalUser.isPresent()) {
            int countUsername = 1;
            while (existsByUsername(username)) {
                username = username + countUsername;
            }

            User user = userMapper.connectedUserToUserEntity(username, firstname, lastname, email, avatarUrl, authenticationMethod);
            return create(user);
        }

        return update(optionalUser.get(), optionalUser.get().getUsername(), firstname, lastname, email, avatarUrl, authenticationMethod);
    }

    /**
     * Update the user information
     * @param user                 The user to update
     * @param username             The username
     * @param firstname            The user firstname
     * @param lastname             The user lastname
     * @param email                The user email
     * @param avatarUrl            The user avatar url
     * @param authenticationMethod The ID provider used
     * @return The updated user
     */
    public User update(final User user, String username, String firstname, String lastname, String email, String avatarUrl, AuthenticationProvider authenticationMethod) {
        User userUpdated = userMapper.connectedUserToUserEntity(username, firstname, lastname, email, avatarUrl, authenticationMethod);
        userUpdated.setRoles(user.getRoles());
        userUpdated.setProjects(user.getProjects());
        userUpdated.setUserSettings(user.getUserSettings());
        userUpdated.setId(user.getId());

        userRepository.save(userUpdated);
        return userUpdated;
    }

    /**
     * Get a user by id
     *
     * @param userId The user id
     * @return The user as optional
     */
    public Optional<User> getOne(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Get a user by username ignoring case
     * @param username The username
     * @return The user as optional
     */
    @Transactional(readOnly = true)
    public Optional<User> getOneByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    /**
     * Get a user by email ignoring case
     * @param email The email
     * @return The user as optional
     */
    @Transactional(readOnly = true)
    public Optional<User> getOneByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    /**
     * Check if a given username exists
     * @param username The username
     * @return true if it is, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Get all paginated users
     *
     * @param search The specification to apply
     * @param pageable The pageable to apply
     * @return The paginated users
     */
    @Transactional
    public Page<User> getAll(String search, Pageable pageable) {
        return userRepository.findAll(new UserSearchSpecification(search), pageable);
    }

    /**
     * Delete a user
     *
     * @param user the user to delete
     */
    @Transactional
    public void deleteUserByUserId(User user) {
        user.getProjects().forEach(project -> projectService.deleteUserFromProject(user, project));
        userRepository.delete(user);
    }

    /**
     * Update a user
     *
     * @param userId    The user id
     * @param username  The username to update
     * @param firstname The firstname to update
     * @param lastname  The lastname to update
     * @param email     The email to update
     * @param roleNames The list of role names for the user
     * @return The user updated
     */
    public Optional<User> updateUser(final Long userId,
                                     final String username,
                                     final String firstname,
                                     final String lastname,
                                     final String email,
                                     final List<UserRoleEnum> roleNames) {
        Optional<User> userOpt = getOne(userId);

        if (!userOpt.isPresent()) {
            return Optional.empty();
        }

        User user = userOpt.get();

        if (StringUtils.isNotBlank(StringUtils.trimToEmpty(username))) {
            user.setUsername(username.trim());
        }

        if (StringUtils.isNotBlank(StringUtils.trimToEmpty(firstname))) {
            user.setFirstname(firstname.trim());
        }

        if (StringUtils.isNotBlank(StringUtils.trimToEmpty(lastname))) {
            user.setLastname(lastname.trim());
        }

        if (StringUtils.isNotBlank(StringUtils.trimToEmpty(email))) {
            user.setEmail(email.trim());
        }

        if (roleNames != null && !roleNames.isEmpty()) {
            this.updateUserRoles(user, roleNames);
        }

        userRepository.save(user);

        return Optional.of(user);
    }

    /**
     * Update the roles for a user
     *
     * @param user      The user
     * @param roleNames The roles to set
     */
    private void updateUserRoles(User user, List<UserRoleEnum> roleNames) {
        Set<Role> rolesToSet = roleNames.stream()
            .map(roleName -> roleService.getRoleByName(roleName.name()).orElse(null))
            .collect(Collectors.toSet());

        if (!rolesToSet.isEmpty()) {
            user.setRoles(rolesToSet);
        }
    }
}
