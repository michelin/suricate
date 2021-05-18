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

package io.suricate.monitoring.services.api;

import io.suricate.monitoring.configuration.security.ConnectedUser;
import io.suricate.monitoring.model.entities.Role;
import io.suricate.monitoring.model.entities.User;
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

import java.util.*;
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

    /**
     * The user repository
     */
    private final UserRepository userRepository;

    /**
     * The user mapper
     */
    private final UserMapper userMapper;

    /**
     * The role service
     */
    private final RoleService roleService;

    /**
     * The project service
     */
    private final ProjectService projectService;

    /**
     * The user setting service
     */
    private final UserSettingService userSettingService;

    /**
     * Constructor
     *
     * @param userRepository     The user repository
     * @param userMapper         The user mapper
     * @param roleService        The role service
     * @param projectService     The projectService to inject
     * @param userSettingService The user setting service
     */
    @Autowired
    public UserService(final UserRepository userRepository,
                       final UserMapper userMapper,
                       final RoleService roleService,
                       final ProjectService projectService,
                       final UserSettingService userSettingService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleService = roleService;
        this.projectService = projectService;
        this.userSettingService = userSettingService;
    }

    /**
     * Register a new user (DATABASE auth Mode)
     *
     * @param user User to register
     * @return The user registered
     */
    public Optional<User> registerNewUserAccount(User user) {
        Optional<Role> role;

        if (userRepository.count() > 0L) {
            role = roleService.getRoleByName(UserRoleEnum.ROLE_USER.name());
        } else {
            role = roleService.getRoleByName(UserRoleEnum.ROLE_ADMIN.name());
        }

        if (!role.isPresent()) {
            LOGGER.debug("Cannot find Role");
            return Optional.empty();
        }

        user.setRoles(Collections.singleton(role.get()));
        userRepository.save(user);

        // Set the default user settings
        user.getUserSettings().addAll(userSettingService.createDefaultSettingsForUser(user));

        return Optional.of(user);
    }

    /**
     * Init a user (LDAP auth mode)
     *
     * @param connectedUser The connected user
     * @return The user
     */
    @Transactional
    public Optional<User> initUser(ConnectedUser connectedUser) {

        if (connectedUser == null) {
            return Optional.empty();
        }

        // Create user
        User user = userMapper.connectedUserToUserEntity(connectedUser);

        UserRoleEnum roleEnumToFind;
        if (userRepository.count() > 0) {
            roleEnumToFind = UserRoleEnum.ROLE_USER;
        } else {
            roleEnumToFind = UserRoleEnum.ROLE_ADMIN;
        }

        Optional<Role> role = roleService.getRoleByName(roleEnumToFind.name());
        if (!role.isPresent()) {
            LOGGER.error("Role {} not available in database", UserRoleEnum.ROLE_USER);
            throw new ObjectNotFoundException(Role.class, roleEnumToFind);
        }

        user.getRoles().add(role.get());
        userRepository.save(user);  // Save user

        // Set the default user settings
        user.getUserSettings().addAll(userSettingService.createDefaultSettingsForUser(user));

        return Optional.of(user);
    }

    /**
     * Update the user information when ldap connection is set
     *
     * @param user          The user to update
     * @param connectedUser The LDAP information
     * @return The user updated
     */
    public Optional<User> updateUserLdapInformation(final User user, final ConnectedUser connectedUser) {
        User userUpdated = userMapper.connectedUserToUserEntity(connectedUser);
        userUpdated.setRoles(user.getRoles());
        userUpdated.setProjects(user.getProjects());
        userUpdated.setUserSettings(user.getUserSettings());
        userUpdated.setId(user.getId());

        userRepository.save(userUpdated);
        return Optional.of(userUpdated);
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
     *
     * @param username The username to find
     * @return The user as optional
     */
    @Transactional
    public Optional<User> getOneByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    /**
     * Get the user id by username
     *
     * @param username The username to find
     * @return The id
     */
    public Long getIdByUsername(String username) {
        return userRepository.getIdByUsername(username);
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
