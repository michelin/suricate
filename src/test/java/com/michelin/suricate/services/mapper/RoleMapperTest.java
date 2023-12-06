package com.michelin.suricate.services.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.michelin.suricate.model.dto.api.role.RoleResponseDto;
import com.michelin.suricate.model.entities.Role;
import com.michelin.suricate.model.enums.UserRoleEnum;
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

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getName()).isEqualTo(UserRoleEnum.ROLE_USER);
        assertThat(actual.getDescription()).isEqualTo("description");
    }
}
