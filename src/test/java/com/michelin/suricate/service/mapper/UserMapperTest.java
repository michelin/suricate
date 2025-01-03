package com.michelin.suricate.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.role.RoleResponseDto;
import com.michelin.suricate.model.dto.api.user.AdminUserResponseDto;
import com.michelin.suricate.model.dto.api.user.UserRequestDto;
import com.michelin.suricate.model.dto.api.user.UserResponseDto;
import com.michelin.suricate.model.entity.Role;
import com.michelin.suricate.model.entity.User;
import com.michelin.suricate.model.enumeration.AuthenticationProvider;
import com.michelin.suricate.model.enumeration.UserRoleEnum;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {
    @Mock
    protected RoleMapper roleMapper;

    @Mock
    protected PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserMapperImpl userMapper;

    @Test
    void shouldToAdminUserDto() {
        Role role = new Role();
        role.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setFirstname("firstname");
        user.setLastname("lastname");
        user.setUsername("username");
        user.setEmail("email");
        user.setRoles(Collections.singleton(role));

        RoleResponseDto roleResponseDto = new RoleResponseDto();
        roleResponseDto.setId(1L);
        roleResponseDto.setDescription("description");
        roleResponseDto.setName(UserRoleEnum.ROLE_USER);

        when(roleMapper.toRoleDto(any()))
            .thenReturn(roleResponseDto);

        AdminUserResponseDto actual = userMapper.toAdminUserDto(user);

        assertEquals(1L, actual.getId());
        assertEquals("email", actual.getEmail());
        assertEquals("firstname", actual.getFirstname());
        assertEquals("lastname", actual.getLastname());
        assertEquals("username", actual.getUsername());
        assertEquals(roleResponseDto, actual.getRoles().getFirst());
    }

    @Test
    void shouldToUserDto() {
        Role role = new Role();
        role.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setFirstname("firstname");
        user.setLastname("lastname");
        user.setUsername("username");
        user.setEmail("email");
        user.setRoles(Collections.singleton(role));

        UserResponseDto actual = userMapper.toUserDto(user);

        assertEquals("firstname", actual.getFirstname());
        assertEquals("lastname", actual.getLastname());
        assertEquals("username", actual.getUsername());
    }

    @Test
    void shouldToUsersDtos() {
        Role role = new Role();
        role.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setFirstname("firstname");
        user.setLastname("lastname");
        user.setUsername("username");
        user.setEmail("email");
        user.setRoles(Collections.singleton(role));

        List<UserResponseDto> actual = userMapper.toUsersDtos(Collections.singletonList(user));

        assertEquals("firstname", actual.getFirst().getFirstname());
        assertEquals("lastname", actual.getFirst().getLastname());
        assertEquals("username", actual.getFirst().getUsername());
    }

    @Test
    void shouldConnectedUserToUserEntity() {
        User actual = userMapper.connectedUserToUserEntity(
            "username",
            "firstname",
            "lastname",
            "email",
            "url",
            AuthenticationProvider.GITLAB
        );

        assertEquals("username", actual.getUsername());
        assertEquals("firstname", actual.getFirstname());
        assertEquals("lastname", actual.getLastname());
        assertEquals("username", actual.getUsername());
        assertEquals("email", actual.getEmail());
        assertEquals("url", actual.getAvatarUrl());
        assertEquals(AuthenticationProvider.GITLAB, actual.getAuthenticationMethod());
    }

    @Test
    void shouldToUserEntity() {
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setFirstname("firstname");
        userRequestDto.setLastname("lastname");
        userRequestDto.setUsername("username");
        userRequestDto.setEmail("email");
        userRequestDto.setPassword("password");
        userRequestDto.setConfirmPassword("password");
        userRequestDto.setRoles(Collections.singletonList(UserRoleEnum.ROLE_USER));

        when(passwordEncoder.encode(any()))
            .thenReturn("encoded");

        User actual = userMapper.toUserEntity(userRequestDto, AuthenticationProvider.GITHUB);

        assertEquals("username", actual.getUsername());
        assertEquals("firstname", actual.getFirstname());
        assertEquals("lastname", actual.getLastname());
        assertEquals("username", actual.getUsername());
        assertEquals("email", actual.getEmail());
        assertEquals("encoded", actual.getPassword());
        assertEquals(AuthenticationProvider.GITHUB, actual.getAuthenticationMethod());
        assertTrue(actual.getRoles().isEmpty());
    }
}
