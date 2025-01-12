/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.michelin.suricate.service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.model.entity.Role;
import com.michelin.suricate.model.entity.User;
import com.michelin.suricate.model.entity.UserSetting;
import com.michelin.suricate.model.enumeration.AuthenticationProvider;
import com.michelin.suricate.model.enumeration.UserRoleEnum;
import com.michelin.suricate.repository.UserRepository;
import com.michelin.suricate.service.mapper.UserMapper;
import com.michelin.suricate.service.specification.UserSearchSpecification;
import com.michelin.suricate.util.exception.ObjectNotFoundException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private RoleService roleService;

    @Mock
    private ProjectService projectService;

    @Mock
    private UserSettingService userSettingService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateAdminUser() {
        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);

        Role role = new Role();
        role.setId(1L);

        User user = new User();
        user.setId(1L);

        when(userRepository.count())
            .thenReturn(0L);
        when(roleService.getRoleByName(any()))
            .thenReturn(Optional.of(role));
        when(userRepository.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));
        when(userSettingService.createDefaultSettingsForUser(any()))
            .thenReturn(Collections.singletonList(userSetting));

        User actual = userService.create(user);

        assertTrue(actual.getRoles().contains(role));
        assertTrue(actual.getUserSettings().contains(userSetting));

        verify(userRepository)
            .count();
        verify(roleService)
            .getRoleByName(UserRoleEnum.ROLE_ADMIN.name());
        verify(userRepository)
            .save(user);
        verify(userSettingService)
            .createDefaultSettingsForUser(user);
    }

    @Test
    void shouldCreateUser() {
        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);

        Role role = new Role();
        role.setId(1L);

        User user = new User();
        user.setId(1L);

        when(userRepository.count())
            .thenReturn(15L);
        when(roleService.getRoleByName(any()))
            .thenReturn(Optional.of(role));
        when(userRepository.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));
        when(userSettingService.createDefaultSettingsForUser(any()))
            .thenReturn(Collections.singletonList(userSetting));

        User actual = userService.create(user);

        assertTrue(actual.getRoles().contains(role));
        assertTrue(actual.getUserSettings().contains(userSetting));

        verify(userRepository)
            .count();
        verify(roleService)
            .getRoleByName(UserRoleEnum.ROLE_USER.name());
        verify(userRepository)
            .save(user);
        verify(userSettingService)
            .createDefaultSettingsForUser(user);
    }

    @Test
    void shouldThrowExceptionWhenCreateUser() {
        User user = new User();
        user.setId(1L);

        when(userRepository.count())
            .thenReturn(15L);
        when(roleService.getRoleByName(any()))
            .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> userService.create(user)
        );

        assertEquals("Role 'ROLE_USER' not found", exception.getMessage());

        verify(userRepository)
            .count();
        verify(roleService)
            .getRoleByName(UserRoleEnum.ROLE_USER.name());
        verify(userRepository, never())
            .save(any());
        verify(userSettingService, never())
            .createDefaultSettingsForUser(any());
    }

    @Test
    void shouldRegisterUser() {
        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);

        Role role = new Role();
        role.setId(1L);

        User user = new User();
        user.setId(1L);

        when(userRepository.findByEmailIgnoreCase(any()))
            .thenReturn(Optional.empty());
        when(userRepository.existsByUsername(any()))
            .thenReturn(false);
        when(userRepository.count())
            .thenReturn(15L);
        when(roleService.getRoleByName(any()))
            .thenReturn(Optional.of(role));
        when(userRepository.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));
        when(userSettingService.createDefaultSettingsForUser(any()))
            .thenReturn(Collections.singletonList(userSetting));

        User actual = userService.registerUser("username", "firstname", "lastname", "email",
            "avatar", AuthenticationProvider.LDAP);

        assertEquals("username", actual.getUsername());
        assertEquals("firstname", actual.getFirstname());
        assertEquals("lastname", actual.getLastname());
        assertEquals("email", actual.getEmail());
        assertEquals("avatar", actual.getAvatarUrl());
        assertEquals(AuthenticationProvider.LDAP, actual.getAuthenticationMethod());
        assertTrue(actual.getRoles().contains(role));
        assertTrue(actual.getUserSettings().contains(userSetting));

        verify(userRepository)
            .findByEmailIgnoreCase("email");
        verify(userRepository)
            .existsByUsername("username");
        verify(userRepository)
            .count();
        verify(roleService)
            .getRoleByName(UserRoleEnum.ROLE_USER.name());
        verify(userRepository)
            .save(argThat(createdUser ->
                createdUser.getUsername().equals("username")
                    && createdUser.getFirstname().equals("firstname")
                    && createdUser.getLastname().equals("lastname")
                    && createdUser.getEmail().equals("email")
                    && createdUser.getAvatarUrl().equals("avatar")
                    && createdUser.getAuthenticationMethod().equals(AuthenticationProvider.LDAP)));
        verify(userSettingService)
            .createDefaultSettingsForUser(any(User.class));
    }

    @Test
    void shouldRegisterUserUsernameAlreadyTaken() {
        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);

        Role role = new Role();
        role.setId(1L);

        User user = new User();
        user.setId(1L);

        when(userRepository.findByEmailIgnoreCase(any()))
            .thenReturn(Optional.empty());
        when(userRepository.existsByUsername(any()))
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(true)
            .thenReturn(false);
        when(userRepository.count())
            .thenReturn(15L);
        when(roleService.getRoleByName(any()))
            .thenReturn(Optional.of(role));
        when(userRepository.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));
        when(userSettingService.createDefaultSettingsForUser(any()))
            .thenReturn(Collections.singletonList(userSetting));

        User actual = userService.registerUser("username", "firstname", "lastname", "email",
            "avatar", AuthenticationProvider.LDAP);

        assertEquals("username3", actual.getUsername());
        assertEquals("firstname", actual.getFirstname());
        assertEquals("lastname", actual.getLastname());
        assertEquals("email", actual.getEmail());
        assertEquals("avatar", actual.getAvatarUrl());
        assertEquals(AuthenticationProvider.LDAP, actual.getAuthenticationMethod());
        assertTrue(actual.getRoles().contains(role));
        assertTrue(actual.getUserSettings().contains(userSetting));

        verify(userRepository)
            .findByEmailIgnoreCase("email");
        verify(userRepository)
            .existsByUsername("username");
        verify(userRepository)
            .existsByUsername("username1");
        verify(userRepository)
            .existsByUsername("username2");
        verify(userRepository)
            .existsByUsername("username3");
        verify(userRepository)
            .count();
        verify(roleService)
            .getRoleByName(UserRoleEnum.ROLE_USER.name());
        verify(userRepository)
            .save(argThat(createdUser ->
                createdUser.getUsername().equals("username3")
                    && createdUser.getFirstname().equals("firstname")
                    && createdUser.getLastname().equals("lastname")
                    && createdUser.getEmail().equals("email")
                    && createdUser.getAvatarUrl().equals("avatar")
                    && createdUser.getAuthenticationMethod().equals(AuthenticationProvider.LDAP)));
        verify(userSettingService)
            .createDefaultSettingsForUser(any(User.class));
    }

    @Test
    void shouldUpdateRegisteredUser() {
        Project project = new Project();
        project.setId(1L);

        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);

        Role role = new Role();
        role.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setUsername("existingUsername");
        user.setRoles(Collections.singleton(role));
        user.setUserSettings(Collections.singleton(userSetting));
        user.setProjects(Collections.singleton(project));

        when(userRepository.findByEmailIgnoreCase(any()))
            .thenReturn(Optional.of(user));
        when(userRepository.save(any()))
            .thenReturn(user);

        User actual = userService.registerUser("username", "firstname", "lastname", "email",
            "avatar", AuthenticationProvider.LDAP);

        assertEquals(1L, actual.getId());
        assertEquals("existingUsername", actual.getUsername());
        assertEquals("firstname", actual.getFirstname());
        assertEquals("lastname", actual.getLastname());
        assertEquals("email", actual.getEmail());
        assertEquals("avatar", actual.getAvatarUrl());
        assertEquals(AuthenticationProvider.LDAP, actual.getAuthenticationMethod());
        assertTrue(actual.getRoles().contains(role));
        assertTrue(actual.getUserSettings().contains(userSetting));
        assertTrue(actual.getProjects().contains(project));

        verify(userRepository)
            .findByEmailIgnoreCase("email");
        verify(userRepository)
            .save(argThat(createdUser ->
                createdUser.getUsername().equals("existingUsername")
                    && createdUser.getFirstname().equals("firstname")
                    && createdUser.getLastname().equals("lastname")
                    && createdUser.getEmail().equals("email")
                    && createdUser.getAvatarUrl().equals("avatar")
                    && createdUser.getAuthenticationMethod().equals(AuthenticationProvider.LDAP)));
    }

    @Test
    void shouldUpdate() {
        Project project = new Project();
        project.setId(1L);

        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);

        Role role = new Role();
        role.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setUsername("existingUsername");
        user.setRoles(Collections.singleton(role));
        user.setUserSettings(Collections.singleton(userSetting));
        user.setProjects(Collections.singleton(project));

        when(userRepository.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));

        User actual = userService.update(user, user.getUsername(), "firstname", "lastname", "email",
            "avatar", AuthenticationProvider.LDAP);

        assertEquals(1L, actual.getId());
        assertEquals("existingUsername", actual.getUsername());
        assertEquals("firstname", actual.getFirstname());
        assertEquals("lastname", actual.getLastname());
        assertEquals("email", actual.getEmail());
        assertEquals("avatar", actual.getAvatarUrl());
        assertEquals(AuthenticationProvider.LDAP, actual.getAuthenticationMethod());
        assertTrue(actual.getRoles().contains(role));
        assertTrue(actual.getUserSettings().contains(userSetting));
        assertTrue(actual.getProjects().contains(project));

        verify(userRepository)
            .save(argThat(createdUser ->
                createdUser.getId().equals(1L)
                    && createdUser.getUsername().equals("existingUsername")
                    && createdUser.getFirstname().equals("firstname")
                    && createdUser.getLastname().equals("lastname")
                    && createdUser.getEmail().equals("email")
                    && createdUser.getAvatarUrl().equals("avatar")
                    && createdUser.getAuthenticationMethod().equals(AuthenticationProvider.LDAP)));
    }

    @Test
    void shouldGetOne() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(any()))
            .thenReturn(Optional.of(user));

        Optional<User> actual = userService.getOne(1L);

        assertTrue(actual.isPresent());
        assertEquals(user, actual.get());

        verify(userRepository)
            .findById(1L);
    }

    @Test
    void shouldGetOneByUsername() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findByUsernameIgnoreCase(any()))
            .thenReturn(Optional.of(user));

        Optional<User> actual = userService.getOneByUsername("username");

        assertTrue(actual.isPresent());
        assertEquals(user, actual.get());

        verify(userRepository)
            .findByUsernameIgnoreCase("username");
    }

    @Test
    void shouldGetOneByEmail() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findByEmailIgnoreCase(any()))
            .thenReturn(Optional.of(user));

        Optional<User> actual = userService.getOneByEmail("email");

        assertTrue(actual.isPresent());
        assertEquals(user, actual.get());

        verify(userRepository)
            .findByEmailIgnoreCase("email");
    }

    @Test
    void shouldExistsByUsername() {
        User user = new User();
        user.setId(1L);

        when(userRepository.existsByUsername(any()))
            .thenReturn(true);

        boolean actual = userService.existsByUsername("username");

        assertTrue(actual);

        verify(userRepository)
            .existsByUsername("username");
    }

    @Test
    void shouldGetAll() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findAll(any(UserSearchSpecification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(user)));

        Page<User> actual = userService.getAll("search", Pageable.unpaged());

        assertFalse(actual.isEmpty());
        assertEquals(user, actual.get().toList().getFirst());

        verify(userRepository)
            .findAll(
                Mockito.<UserSearchSpecification>argThat(specification -> specification.getSearch().equals("search")
                    && specification.getAttributes().isEmpty()),
                Mockito.argThat(pageable -> pageable.equals(Pageable.unpaged())));
    }

    @Test
    void shouldDeleteUserByUserId() {
        Project project = new Project();
        project.setId(1L);
        Set<Project> projects = Collections.singleton(project);

        User user = new User();
        user.setId(1L);
        user.setProjects(projects);

        userService.deleteUserByUserId(user);

        verify(projectService)
            .deleteUserFromProject(user, project);
        verify(userRepository)
            .delete(user);
    }

    @Test
    void shouldUpdateUser() {
        Role role = new Role();
        role.setId(1L);

        User user = new User();

        when(userRepository.findById(any()))
            .thenReturn(Optional.of(user));
        when(roleService.getRoleByName(any()))
            .thenReturn(Optional.of(role));
        when(userRepository.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));

        Optional<User> actual = userService.updateUser(1L, "username", "firstname", "lastname",
            "email", Collections.singletonList(UserRoleEnum.ROLE_USER));

        assertTrue(actual.isPresent());
        assertEquals("username", actual.get().getUsername());
        assertEquals("firstname", actual.get().getFirstname());
        assertEquals("lastname", actual.get().getLastname());
        assertEquals("email", actual.get().getEmail());
        assertTrue(actual.get().getRoles().contains(role));

        verify(userRepository)
            .findById(1L);
        verify(roleService)
            .getRoleByName(UserRoleEnum.ROLE_USER.name());
        verify(userRepository)
            .save(user);
    }

    @Test
    void shouldUpdateUserRoleNotFound() {
        Role role = new Role();
        role.setId(1L);

        User user = new User();

        when(userRepository.findById(any()))
            .thenReturn(Optional.of(user));
        when(roleService.getRoleByName(any()))
            .thenReturn(Optional.empty());
        when(userRepository.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));

        Optional<User> actual = userService.updateUser(1L, "username", "firstname", "lastname",
            "email", Collections.singletonList(UserRoleEnum.ROLE_USER));

        assertTrue(actual.isPresent());
        assertEquals("username", actual.get().getUsername());
        assertEquals("firstname", actual.get().getFirstname());
        assertEquals("lastname", actual.get().getLastname());
        assertEquals("email", actual.get().getEmail());
        assertTrue(actual.get().getRoles().contains(null));

        verify(userRepository)
            .findById(1L);
        verify(roleService)
            .getRoleByName(UserRoleEnum.ROLE_USER.name());
        verify(userRepository)
            .save(user);
    }

    @Test
    void shouldUpdateUserWhenNullInputs() {
        Role role = new Role();
        role.setId(1L);

        User user = new User();

        when(userRepository.findById(any()))
            .thenReturn(Optional.of(user));
        when(userRepository.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));

        Optional<User> actual = userService.updateUser(1L, null, null, null, null, Collections.emptyList());

        assertTrue(actual.isPresent());
        assertNull(actual.get().getUsername());
        assertNull(actual.get().getFirstname());
        assertNull(actual.get().getLastname());
        assertNull(actual.get().getEmail());
        assertTrue(actual.get().getRoles().isEmpty());

        verify(userRepository)
            .findById(1L);
        verify(userRepository)
            .save(user);
    }

    @Test
    void shouldNotUpdateUserWhenNotFound() {
        Role role = new Role();
        role.setId(1L);

        when(userRepository.findById(any()))
            .thenReturn(Optional.empty());

        Optional<User> actual = userService.updateUser(1L, "username", "firstname", "lastname",
            "email", Collections.singletonList(UserRoleEnum.ROLE_USER));

        assertTrue(actual.isEmpty());

        verify(userRepository)
            .findById(1L);
        verify(roleService, never())
            .getRoleByName(UserRoleEnum.ROLE_USER.name());
        verify(userRepository, never())
            .save(argThat(user -> user.getId().equals(1L)
                && user.getUsername().equals("username")
                && user.getFirstname().equals("firstname")
                && user.getLastname().equals("lastname")
                && user.getEmail().equals("email")
                && user.getAuthenticationMethod().equals(AuthenticationProvider.LDAP)));
    }
}
