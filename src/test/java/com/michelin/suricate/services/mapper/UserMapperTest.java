package com.michelin.suricate.services.mapper;

import com.michelin.suricate.model.dto.api.role.RoleResponseDto;
import com.michelin.suricate.model.dto.api.user.AdminUserResponseDto;
import com.michelin.suricate.model.dto.api.user.UserRequestDto;
import com.michelin.suricate.model.dto.api.user.UserResponseDto;
import com.michelin.suricate.model.entities.Role;
import com.michelin.suricate.model.entities.User;
import com.michelin.suricate.model.enums.AuthenticationProvider;
import com.michelin.suricate.model.enums.UserRoleEnum;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {
    @Mock
    protected RoleMapper roleMapper;

    @Mock
    protected PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserMapperImpl userMapper;

    @Test
    void shouldToAdminUserDTO() {
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

        when(roleMapper.toRoleDTO(any()))
                .thenReturn(roleResponseDto);

        AdminUserResponseDto actual = userMapper.toAdminUserDTO(user);

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getEmail()).isEqualTo("email");
        assertThat(actual.getFirstname()).isEqualTo("firstname");
        assertThat(actual.getLastname()).isEqualTo("lastname");
        assertThat(actual.getUsername()).isEqualTo("username");
        Assertions.assertThat(actual.getRoles().get(0)).isEqualTo(roleResponseDto);
    }

    @Test
    void shouldToUserDTO() {
        Role role = new Role();
        role.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setFirstname("firstname");
        user.setLastname("lastname");
        user.setUsername("username");
        user.setEmail("email");
        user.setRoles(Collections.singleton(role));

        UserResponseDto actual = userMapper.toUserDTO(user);

        assertThat(actual.getFirstname()).isEqualTo("firstname");
        assertThat(actual.getLastname()).isEqualTo("lastname");
        assertThat(actual.getUsername()).isEqualTo("username");
    }

    @Test
    void shouldToUsersDTOs() {
        Role role = new Role();
        role.setId(1L);

        User user = new User();
        user.setId(1L);
        user.setFirstname("firstname");
        user.setLastname("lastname");
        user.setUsername("username");
        user.setEmail("email");
        user.setRoles(Collections.singleton(role));

        List<UserResponseDto> actual = userMapper.toUsersDTOs(Collections.singletonList(user));

        assertThat(actual.get(0).getFirstname()).isEqualTo("firstname");
        assertThat(actual.get(0).getLastname()).isEqualTo("lastname");
        assertThat(actual.get(0).getUsername()).isEqualTo("username");
    }

    @Test
    void shouldConnectedUserToUserEntity() {
        User actual = userMapper.connectedUserToUserEntity("username", "firstname", "lastname", "email", "url",
                AuthenticationProvider.GITLAB);

        assertThat(actual.getUsername()).isEqualTo("username");
        assertThat(actual.getFirstname()).isEqualTo("firstname");
        assertThat(actual.getLastname()).isEqualTo("lastname");
        assertThat(actual.getUsername()).isEqualTo("username");
        assertThat(actual.getEmail()).isEqualTo("email");
        assertThat(actual.getAvatarUrl()).isEqualTo("url");
        assertThat(actual.getAuthenticationMethod()).isEqualTo(AuthenticationProvider.GITLAB);
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

        assertThat(actual.getUsername()).isEqualTo("username");
        assertThat(actual.getFirstname()).isEqualTo("firstname");
        assertThat(actual.getLastname()).isEqualTo("lastname");
        assertThat(actual.getUsername()).isEqualTo("username");
        assertThat(actual.getEmail()).isEqualTo("email");
        assertThat(actual.getPassword()).isEqualTo("encoded");
        assertThat(actual.getAuthenticationMethod()).isEqualTo(AuthenticationProvider.GITHUB);
        assertThat(actual.getRoles()).isEmpty();
    }
}
