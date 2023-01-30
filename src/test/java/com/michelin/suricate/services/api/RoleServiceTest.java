package com.michelin.suricate.services.api;

import com.michelin.suricate.model.entities.Role;
import com.michelin.suricate.model.entities.Role_;
import com.michelin.suricate.repositories.RoleRepository;
import com.michelin.suricate.services.specifications.RoleSearchSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.metamodel.SingularAttribute;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
    @Mock
    private SingularAttribute<Role, String> name;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void shouldGetRoleByName() {
        Role role = new Role();
        role.setId(1L);

        when(roleRepository.findByName(any()))
                .thenReturn(Optional.of(role));

        Optional<Role> actual = roleService.getRoleByName("name");

        assertThat(actual)
                .isPresent()
                .contains(role);

        verify(roleRepository, times(1))
                .findByName("name");
    }

    @Test
    void shouldGetRoles() {
        Role role = new Role();
        role.setId(1L);

        Role_.name = name;
        when(roleRepository.findAll(any(RoleSearchSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(role)));

        Page<Role> actual = roleService.getRoles("search", Pageable.unpaged());

        assertThat(actual)
                .isNotEmpty()
                .contains(role);

        verify(roleRepository, times(1))
                .findAll(Mockito.<RoleSearchSpecification>argThat(specification -> specification.getSearch().equals("search") &&
                                specification.getAttributes().contains(name.getName())),
                        Mockito.<Pageable>argThat(pageable -> pageable.equals(Pageable.unpaged())));
    }

    @Test
    void shouldGetOneById() {
        Role role = new Role();
        role.setId(1L);

        when(roleRepository.findById(any()))
                .thenReturn(Optional.of(role));

        Optional<Role> actual = roleService.getOneById(1L);

        assertThat(actual)
                .isNotEmpty()
                .contains(role);

        verify(roleRepository, times(1))
                .findById(1L);
    }
}
