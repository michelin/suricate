package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.role.RoleResponseDto;
import io.suricate.monitoring.model.entities.Role;
import io.suricate.monitoring.model.enums.UserRoleEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RoleMapperTest {
    @InjectMocks
    private RoleMapperImpl roleMapper;

    @Test
    void shouldToRoleDTO() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        role.setDescription("description");

        RoleResponseDto actual = roleMapper.toRoleDTO(role);

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getName()).isEqualTo(UserRoleEnum.ROLE_USER);
        assertThat(actual.getDescription()).isEqualTo("description");
    }
}
