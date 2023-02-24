package com.michelin.suricate.controllers;

import com.michelin.suricate.model.dto.api.role.RoleResponseDto;
import com.michelin.suricate.model.dto.api.user.UserResponseDto;
import com.michelin.suricate.model.entities.Role;
import com.michelin.suricate.services.api.RoleService;
import com.michelin.suricate.services.mapper.RoleMapper;
import com.michelin.suricate.services.mapper.UserMapper;
import com.michelin.suricate.utils.exceptions.ObjectNotFoundException;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleControllerTest {
    @Mock
    private RoleService roleService;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private RoleController roleController;

    @Test
    void shouldGetRoles() {
        RoleResponseDto roleResponseDto = new RoleResponseDto();
        roleResponseDto.setId(1L);

        Role role = new Role();
        role.setId(1L);

        when(roleService.getRoles(any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(role)));
        when(roleMapper.toRoleDTO(any()))
                .thenReturn(roleResponseDto);

        Page<RoleResponseDto> actual = roleController.getRoles("search", Pageable.unpaged());

        assertThat(actual).isNotEmpty();
        assertThat(actual.get()).hasSize(1);
        assertThat(actual.get().collect(Collectors.toList()).get(0)).isEqualTo(roleResponseDto);
    }

    @Test
    void shouldGetOneNotFound() {
        when(roleService.getOneById(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleController.getOne(1L))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Role '1' not found");
    }

    @Test
    void shouldGetOne() {
        RoleResponseDto roleResponseDto = new RoleResponseDto();
        roleResponseDto.setId(1L);

        Role role = new Role();
        role.setId(1L);

        when(roleService.getOneById(any()))
                .thenReturn(Optional.of(role));
        when(roleMapper.toRoleDTO(any()))
                .thenReturn(roleResponseDto);

        ResponseEntity<RoleResponseDto> actual = roleController.getOne(1L);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo(roleResponseDto);
    }

    @Test
    void shouldGetUsersByRoleNotFound() {
        when(roleService.getOneById(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleController.getUsersByRole(1L))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessage("Role '1' not found");
    }

    @Test
    void shouldGetUsersByRole() {
        Role role = new Role();
        role.setId(1L);

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername("username");

        when(roleService.getOneById(any()))
                .thenReturn(Optional.of(role));
        when(userMapper.toUsersDTOs(any()))
                .thenReturn(Collections.singletonList(userResponseDto));

        ResponseEntity<List<UserResponseDto>> actual = roleController.getUsersByRole(1L);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).contains(userResponseDto);
    }
}
