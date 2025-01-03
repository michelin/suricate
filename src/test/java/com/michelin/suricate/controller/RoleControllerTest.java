package com.michelin.suricate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.role.RoleResponseDto;
import com.michelin.suricate.model.dto.api.user.UserResponseDto;
import com.michelin.suricate.model.entity.Role;
import com.michelin.suricate.service.api.RoleService;
import com.michelin.suricate.service.mapper.RoleMapper;
import com.michelin.suricate.service.mapper.UserMapper;
import com.michelin.suricate.util.exception.ObjectNotFoundException;
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
        when(roleMapper.toRoleDto(any()))
            .thenReturn(roleResponseDto);

        Page<RoleResponseDto> actual = roleController.getRoles("search", Pageable.unpaged());

        assertFalse(actual.isEmpty());
        assertEquals(1, actual.get().count());
        assertEquals(roleResponseDto, actual.get().toList().getFirst());
    }

    @Test
    void shouldGetOneNotFound() {
        when(roleService.getOneById(any()))
            .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> roleController.getOne(1L)
        );

        assertEquals("Role '1' not found", exception.getMessage());
    }

    @Test
    void shouldGetOne() {
        RoleResponseDto roleResponseDto = new RoleResponseDto();
        roleResponseDto.setId(1L);

        Role role = new Role();
        role.setId(1L);

        when(roleService.getOneById(any()))
            .thenReturn(Optional.of(role));
        when(roleMapper.toRoleDto(any()))
            .thenReturn(roleResponseDto);

        ResponseEntity<RoleResponseDto> actual = roleController.getOne(1L);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(roleResponseDto, actual.getBody());
    }

    @Test
    void shouldGetUsersByRoleNotFound() {
        when(roleService.getOneById(any()))
            .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(
            ObjectNotFoundException.class,
            () -> roleController.getUsersByRole(1L)
        );

        assertEquals("Role '1' not found", exception.getMessage());
    }

    @Test
    void shouldGetUsersByRole() {
        Role role = new Role();
        role.setId(1L);

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername("username");

        when(roleService.getOneById(any()))
            .thenReturn(Optional.of(role));
        when(userMapper.toUsersDtos(any()))
            .thenReturn(Collections.singletonList(userResponseDto));

        ResponseEntity<List<UserResponseDto>> actual = roleController.getUsersByRole(1L);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertTrue(actual.getBody().contains(userResponseDto));
    }
}
