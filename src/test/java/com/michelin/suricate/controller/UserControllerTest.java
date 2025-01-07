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

package com.michelin.suricate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.token.PersonalAccessTokenRequestDto;
import com.michelin.suricate.model.dto.api.token.PersonalAccessTokenResponseDto;
import com.michelin.suricate.model.dto.api.user.AdminUserResponseDto;
import com.michelin.suricate.model.dto.api.user.UserRequestDto;
import com.michelin.suricate.model.dto.api.user.UserResponseDto;
import com.michelin.suricate.model.dto.api.user.UserSettingRequestDto;
import com.michelin.suricate.model.dto.api.user.UserSettingResponseDto;
import com.michelin.suricate.model.entity.PersonalAccessToken;
import com.michelin.suricate.model.entity.Role;
import com.michelin.suricate.model.entity.Setting;
import com.michelin.suricate.model.entity.User;
import com.michelin.suricate.model.entity.UserSetting;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.service.api.PersonalAccessTokenService;
import com.michelin.suricate.service.api.SettingService;
import com.michelin.suricate.service.api.UserService;
import com.michelin.suricate.service.api.UserSettingService;
import com.michelin.suricate.service.mapper.PersonalAccessTokenMapper;
import com.michelin.suricate.service.mapper.UserMapper;
import com.michelin.suricate.service.mapper.UserSettingMapper;
import com.michelin.suricate.service.token.PersonalAccessTokenHelperService;
import com.michelin.suricate.util.exception.EmailAlreadyExistException;
import com.michelin.suricate.util.exception.ObjectNotFoundException;
import com.michelin.suricate.util.exception.UsernameAlreadyExistException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    private UserService userService;

    @Mock
    private UserSettingService userSettingService;

    @Mock
    private SettingService settingService;

    @Mock
    private PersonalAccessTokenHelperService patHelperService;

    @Mock
    private PersonalAccessTokenService patService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserSettingMapper userSettingMapper;

    @Mock
    private PersonalAccessTokenMapper personalAccessTokenMapper;

    @InjectMocks
    private UserController userController;

    @Test
    void shouldGetAllForAdmins() {
        AdminUserResponseDto adminUserResponseDto = new AdminUserResponseDto();
        adminUserResponseDto.setId(1L);

        User user = new User();
        user.setId(1L);

        when(userService.getAll(any(), any()))
            .thenReturn(new PageImpl<>(Collections.singletonList(user)));
        when(userMapper.toAdminUserDto(any()))
            .thenReturn(adminUserResponseDto);

        Page<AdminUserResponseDto> actual = userController.getAllForAdmins("search", Pageable.unpaged());

        assertFalse(actual.isEmpty());
        assertEquals(1, actual.get().count());
        assertEquals(adminUserResponseDto, actual.get().toList().getFirst());
    }

    @Test
    void shouldGetAll() {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername("username");

        User user = new User();
        user.setId(1L);

        when(userService.getAll(any(), any()))
            .thenReturn(new PageImpl<>(Collections.singletonList(user)));
        when(userMapper.toUserDto(any()))
            .thenReturn(userResponseDto);

        Page<UserResponseDto> actual = userController.getAll("search", Pageable.unpaged());

        assertFalse(actual.isEmpty());
        assertEquals(1, actual.get().count());
        assertEquals(userResponseDto, actual.get().toList().getFirst());
    }

    @Test
    void shouldGetOneNotFound() {
        when(userService.getOne(any()))
            .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> userController.getOne(1L)
        );

        assertEquals("User '1' not found", exception.getMessage());
    }

    @Test
    void shouldGetOne() {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername("username");

        User user = new User();
        user.setId(1L);

        when(userService.getOne(any()))
            .thenReturn(Optional.of(user));
        when(userMapper.toUserDto(any()))
            .thenReturn(userResponseDto);

        ResponseEntity<UserResponseDto> actual = userController.getOne(1L);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(userResponseDto, actual.getBody());
    }

    @Test
    void shouldUpdateOneNotFound() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("username");

        when(userService.updateUser(any(), any(), any(), any(), any(), any()))
            .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> userController.updateOne(1L, userRequestDto)
        );

        assertEquals("User '1' not found", exception.getMessage());
    }

    @Test
    void shouldUpdateOne() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("username");

        User user = new User();
        user.setId(1L);

        when(userService.updateUser(any(), any(), any(), any(), any(), any()))
            .thenReturn(Optional.of(user));

        ResponseEntity<Void> actual = userController.updateOne(1L, userRequestDto);

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        assertNull(actual.getBody());
    }

    @Test
    void shouldDeleteOneNotFound() {
        when(userService.getOne(any()))
            .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> userController.deleteOne(1L)
        );

        assertEquals("User '1' not found", exception.getMessage());
    }

    @Test
    void shouldDeleteOne() {
        User user = new User();
        user.setId(1L);

        when(userService.getOne(any()))
            .thenReturn(Optional.of(user));

        ResponseEntity<Void> actual = userController.deleteOne(1L);

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        assertNull(actual.getBody());
    }

    @Test
    void shouldGetUserSettingsNotFound() {
        when(userSettingService.getUserSettingsByUsername(any()))
            .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> userController.getUserSettings("username")
        );

        assertEquals("UserSetting 'username' not found", exception.getMessage());
    }

    @Test
    void shouldGetUserSettings() {
        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);

        UserSettingResponseDto userSettingResponseDto = new UserSettingResponseDto();
        userSettingResponseDto.setId(1L);

        when(userSettingService.getUserSettingsByUsername(any()))
            .thenReturn(Optional.of(Collections.singletonList(userSetting)));
        when(userSettingMapper.toUserSettingsDtos(any()))
            .thenReturn(Collections.singletonList(userSettingResponseDto));

        ResponseEntity<List<UserSettingResponseDto>> actual = userController.getUserSettings("username");

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertTrue(actual.getBody().contains(userSettingResponseDto));
    }

    @Test
    void shouldUpdateUserSettingsNotFound() {
        UserSettingRequestDto userSettingRequestDto = new UserSettingRequestDto();
        userSettingRequestDto.setAllowedSettingValueId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(userService.getOneByUsername(any()))
            .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> userController.updateUserSettings(localUser, "username", 1L, userSettingRequestDto)
        );

        assertEquals("User 'username' not found", exception.getMessage());
    }

    @Test
    void shouldUpdateUserSettingsAccessDenied() {
        UserSettingRequestDto userSettingRequestDto = new UserSettingRequestDto();
        userSettingRequestDto.setAllowedSettingValueId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(userService.getOneByUsername(any()))
            .thenReturn(Optional.of(user));

        AccessDeniedException exception = assertThrows(
            AccessDeniedException.class,
            () -> userController.updateUserSettings(localUser, "username2", 1L, userSettingRequestDto)
        );

        assertEquals("User username is not allowed to modify this resource", exception.getMessage());
    }

    @Test
    void shouldUpdateUserSettingsSettingNotFound() {
        UserSettingRequestDto userSettingRequestDto = new UserSettingRequestDto();
        userSettingRequestDto.setAllowedSettingValueId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(userService.getOneByUsername(any()))
            .thenReturn(Optional.of(user));
        when(settingService.getOneById(any()))
            .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> userController.updateUserSettings(localUser, "username", 1L, userSettingRequestDto)
        );

        assertEquals("Setting '1' not found", exception.getMessage());
    }

    @Test
    void shouldUpdateUserSettings() {
        Setting setting = new Setting();
        setting.setId(1L);

        UserSettingRequestDto userSettingRequestDto = new UserSettingRequestDto();
        userSettingRequestDto.setAllowedSettingValueId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(userService.getOneByUsername(any()))
            .thenReturn(Optional.of(user));
        when(settingService.getOneById(any()))
            .thenReturn(Optional.of(setting));

        ResponseEntity<UserResponseDto> actual =
            userController.updateUserSettings(localUser, "username", 1L, userSettingRequestDto);

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        assertNull(actual.getBody());
    }

    @Test
    void shouldSignUpUsernameAlreadyExist() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("username");

        User user = new User();
        user.setId(1L);

        when(userService.getOneByUsername(any()))
            .thenReturn(Optional.of(user));

        UsernameAlreadyExistException exception = assertThrows(
            UsernameAlreadyExistException.class,
            () -> userController.signUp(userRequestDto)
        );

        assertEquals("Username 'username' already exist", exception.getMessage());
    }

    @Test
    void shouldSignUpEmailAlreadyExist() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("username");
        userRequestDto.setEmail("email");

        User user = new User();
        user.setId(1L);

        when(userService.getOneByUsername(any()))
            .thenReturn(Optional.empty());
        when(userService.getOneByEmail(any()))
            .thenReturn(Optional.of(user));

        EmailAlreadyExistException exception = assertThrows(
            EmailAlreadyExistException.class,
            () -> userController.signUp(userRequestDto)
        );

        assertEquals("Email 'email' already exist", exception.getMessage());
    }

    @Test
    void shouldSignUp() {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername("username");

        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("username");
        userRequestDto.setEmail("email");

        User user = new User();
        user.setId(1L);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        when(userService.getOneByUsername(any()))
            .thenReturn(Optional.empty());
        when(userService.getOneByEmail(any()))
            .thenReturn(Optional.empty());
        when(userMapper.toUserEntity(any(), any()))
            .thenReturn(user);
        when(userService.create(any()))
            .thenAnswer(answer -> answer.getArgument(0));
        when(userMapper.toUserDto(any()))
            .thenReturn(userResponseDto);

        ResponseEntity<UserResponseDto> actual = userController.signUp(userRequestDto);

        assertEquals(HttpStatus.CREATED, actual.getStatusCode());
        assertEquals(userResponseDto, actual.getBody());
    }

    @Test
    void shouldGetPersonalAccessTokens() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        PersonalAccessToken personalAccessToken = new PersonalAccessToken();
        personalAccessToken.setId(1L);

        PersonalAccessTokenResponseDto personalAccessTokenResponseDto = new PersonalAccessTokenResponseDto();
        personalAccessTokenResponseDto.setName("name");

        when(patService.findAllByUser(any()))
            .thenReturn(Collections.singletonList(personalAccessToken));
        when(personalAccessTokenMapper.toPersonalAccessTokensDtos(any()))
            .thenReturn(Collections.singletonList(personalAccessTokenResponseDto));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        ResponseEntity<List<PersonalAccessTokenResponseDto>> actual = userController.getPersonalAccessTokens(localUser);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertTrue(actual.getBody().contains(personalAccessTokenResponseDto));
    }

    @Test
    void shouldCreatePersonalAccessToken() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        PersonalAccessTokenRequestDto personalAccessTokenRequestDto = new PersonalAccessTokenRequestDto();
        personalAccessTokenRequestDto.setName("name");

        PersonalAccessToken personalAccessToken = new PersonalAccessToken();
        personalAccessToken.setId(1L);

        PersonalAccessTokenResponseDto personalAccessTokenResponseDto = new PersonalAccessTokenResponseDto();
        personalAccessTokenResponseDto.setName("name");

        when(patHelperService.createPersonalAccessToken())
            .thenReturn("token");
        when(patHelperService.computePersonAccessTokenChecksum(any()))
            .thenReturn(15L);
        when(patService.create(any(), any(), any()))
            .thenReturn(personalAccessToken);
        when(personalAccessTokenMapper.toPersonalAccessTokenDto(any(), any()))
            .thenReturn(personalAccessTokenResponseDto);

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        ResponseEntity<PersonalAccessTokenResponseDto> actual =
            userController.createPersonalAccessToken(localUser, personalAccessTokenRequestDto);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(personalAccessTokenResponseDto, actual.getBody());
    }

    @Test
    void shouldDeletePersonalAccessTokenNotFound() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(patService.findByNameAndUser(any(), any()))
            .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> userController.deletePersonalAccessToken(localUser, "token")
        );

        assertEquals("PersonalAccessToken 'token' not found", exception.getMessage());
    }

    @Test
    void shouldDeletePersonalAccessToken() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        PersonalAccessToken personalAccessToken = new PersonalAccessToken();
        personalAccessToken.setId(1L);

        when(patService.findByNameAndUser(any(), any()))
            .thenReturn(Optional.of(personalAccessToken));

        ResponseEntity<Void> actual = userController.deletePersonalAccessToken(localUser, "token");

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
        assertNull(actual.getBody());
    }
}
