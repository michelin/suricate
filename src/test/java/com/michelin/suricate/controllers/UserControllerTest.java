package com.michelin.suricate.controllers;

import com.michelin.suricate.model.dto.api.token.PersonalAccessTokenRequestDto;
import com.michelin.suricate.model.dto.api.token.PersonalAccessTokenResponseDto;
import com.michelin.suricate.model.dto.api.user.*;
import com.michelin.suricate.model.entities.*;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.services.api.PersonalAccessTokenService;
import com.michelin.suricate.services.api.SettingService;
import com.michelin.suricate.services.api.UserService;
import com.michelin.suricate.services.api.UserSettingService;
import com.michelin.suricate.services.mapper.PersonalAccessTokenMapper;
import com.michelin.suricate.services.mapper.UserMapper;
import com.michelin.suricate.services.mapper.UserSettingMapper;
import com.michelin.suricate.services.token.PersonalAccessTokenHelperService;
import com.michelin.suricate.utils.exceptions.EmailAlreadyExistException;
import com.michelin.suricate.utils.exceptions.ObjectNotFoundException;
import com.michelin.suricate.utils.exceptions.UsernameAlreadyExistException;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
        when(userMapper.toAdminUserDTO(any()))
                .thenReturn(adminUserResponseDto);

        Page<AdminUserResponseDto> actual = userController.getAllForAdmins("search", Pageable.unpaged());

        assertThat(actual).isNotEmpty();
        assertThat(actual.get()).hasSize(1);
        assertThat(actual.get().collect(Collectors.toList()).get(0)).isEqualTo(adminUserResponseDto);
    }

    @Test
    void shouldGetAll() {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername("username");

        User user = new User();
        user.setId(1L);

        when(userService.getAll(any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(user)));
        when(userMapper.toUserDTO(any()))
                .thenReturn(userResponseDto);

        Page<UserResponseDto> actual = userController.getAll("search", Pageable.unpaged());

        assertThat(actual).isNotEmpty();
        assertThat(actual.get()).hasSize(1);
        assertThat(actual.get().collect(Collectors.toList()).get(0)).isEqualTo(userResponseDto);
    }

    @Test
    void shouldGetOneNotFound() {
        when(userService.getOne(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userController.getOne(1L))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("User '1' not found");
    }

    @Test
    void shouldGetOne() {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername("username");

        User user = new User();
        user.setId(1L);

        when(userService.getOne(any()))
                .thenReturn(Optional.of(user));
        when(userMapper.toUserDTO(any()))
                .thenReturn(userResponseDto);

        ResponseEntity<UserResponseDto> actual = userController.getOne(1L);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo(userResponseDto);
    }

    @Test
    void shouldUpdateOneNotFound() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("username");

        when(userService.updateUser(any(), any(), any(), any(), any(), any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userController.updateOne(1L, userRequestDto))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("User '1' not found");
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

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    void shouldDeleteOneNotFound() {
        when(userService.getOne(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userController.deleteOne(1L))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("User '1' not found");
    }

    @Test
    void shouldDeleteOne() {
        User user = new User();
        user.setId(1L);

        when(userService.getOne(any()))
                .thenReturn(Optional.of(user));
        doNothing().when(userService)
                .deleteUserByUserId(any());

        ResponseEntity<Void> actual = userController.deleteOne(1L);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    void shouldGetUserSettingsNotFound() {
        when(userSettingService.getUserSettingsByUsername(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userController.getUserSettings("username"))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("UserSetting 'username' not found");
    }

    @Test
    void shouldGetUserSettings() {
        UserSetting userSetting = new UserSetting();
        userSetting.setId(1L);

        UserSettingResponseDto userSettingResponseDto = new UserSettingResponseDto();
        userSettingResponseDto.setId(1L);

        when(userSettingService.getUserSettingsByUsername(any()))
                .thenReturn(Optional.of(Collections.singletonList(userSetting)));
        when(userSettingMapper.toUserSettingsDTOs(any()))
                .thenReturn(Collections.singletonList(userSettingResponseDto));

        ResponseEntity<List<UserSettingResponseDto>> actual = userController.getUserSettings("username");

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).contains(userSettingResponseDto);
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

        assertThatThrownBy(() -> userController.updateUserSettings(localUser, "username", 1L, userSettingRequestDto))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("User 'username' not found");
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

        assertThatThrownBy(() -> userController.updateUserSettings(localUser, "username2", 1L, userSettingRequestDto))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("User username is not allowed to modify this resource");
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

        assertThatThrownBy(() -> userController.updateUserSettings(localUser, "username", 1L, userSettingRequestDto))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Setting '1' not found");
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
        doNothing().when(userSettingService)
                .updateUserSetting(any(), any(), any());

        ResponseEntity<UserResponseDto> actual = userController.updateUserSettings(localUser, "username", 1L, userSettingRequestDto);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(actual.getBody()).isNull();
    }

    @Test
    void shouldSignUpUsernameAlreadyExist() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("username");

        User user = new User();
        user.setId(1L);

        when(userService.getOneByUsername(any()))
                .thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userController.signUp(userRequestDto))
                .isInstanceOf(UsernameAlreadyExistException.class)
                .hasMessage("Username 'username' already exist");
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

        assertThatThrownBy(() -> userController.signUp(userRequestDto))
                .isInstanceOf(EmailAlreadyExistException.class)
                .hasMessage("Email 'email' already exist");
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
        when(userMapper.toUserDTO(any()))
                .thenReturn(userResponseDto);

        ResponseEntity<UserResponseDto> actual = userController.signUp(userRequestDto);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(actual.getBody()).isEqualTo(userResponseDto);
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

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        PersonalAccessToken personalAccessToken = new PersonalAccessToken();
        personalAccessToken.setId(1L);

        PersonalAccessTokenResponseDto personalAccessTokenResponseDto = new PersonalAccessTokenResponseDto();
        personalAccessTokenResponseDto.setName("name");

        when(patService.findAllByUser(any()))
                .thenReturn(Collections.singletonList(personalAccessToken));
        when(personalAccessTokenMapper.toPersonalAccessTokensDTOs(any()))
                .thenReturn(Collections.singletonList(personalAccessTokenResponseDto));

        ResponseEntity<List<PersonalAccessTokenResponseDto>> actual = userController.getPersonalAccessTokens(localUser);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).contains(personalAccessTokenResponseDto);
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

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

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
        when(personalAccessTokenMapper.toPersonalAccessTokenDTO(any(), any()))
                .thenReturn(personalAccessTokenResponseDto);

        ResponseEntity<PersonalAccessTokenResponseDto> actual = userController.createPersonalAccessToken(localUser, personalAccessTokenRequestDto);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo(personalAccessTokenResponseDto);
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

        assertThatThrownBy(() -> userController.deletePersonalAccessToken(localUser, "token"))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("PersonalAccessToken 'token' not found");
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
        doNothing().when(patService)
                .deleteById(any());

        ResponseEntity<Void> actual = userController.deletePersonalAccessToken(localUser, "token");

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(actual.getBody()).isNull();
    }
}
