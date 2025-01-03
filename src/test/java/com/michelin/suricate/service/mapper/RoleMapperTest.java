package com.michelin.suricate.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.michelin.suricate.model.dto.api.role.RoleResponseDto;
import com.michelin.suricate.model.entity.Role;
import com.michelin.suricate.model.enumeration.UserRoleEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RoleMapperTest {
    @InjectMocks
    private RoleMapperImpl roleMapper;

    @Test
    void shouldToRoleDto() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        role.setDescription("description");

        RoleResponseDto actual = roleMapper.toRoleDto(role);

        assertEquals(1L, actual.getId());
        assertEquals(UserRoleEnum.ROLE_USER, actual.getName());
        assertEquals("description", actual.getDescription());
    }
}
