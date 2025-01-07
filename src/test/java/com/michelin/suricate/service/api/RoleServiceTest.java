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

package com.michelin.suricate.service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entity.Role;
import com.michelin.suricate.model.entity.Role_;
import com.michelin.suricate.repository.RoleRepository;
import com.michelin.suricate.service.specification.RoleSearchSpecification;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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

        assertTrue(actual.isPresent());
        assertEquals(role, actual.get());

        verify(roleRepository)
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

        assertFalse(actual.isEmpty());
        assertEquals(role, actual.get().toList().getFirst());

        verify(roleRepository)
            .findAll(
                Mockito.<RoleSearchSpecification>argThat(specification -> specification.getSearch().equals("search")
                    && specification.getAttributes().contains(name.getName())),
                Mockito.<Pageable>argThat(pageable -> pageable.equals(Pageable.unpaged())));
    }

    @Test
    void shouldGetOneById() {
        Role role = new Role();
        role.setId(1L);

        when(roleRepository.findById(any()))
            .thenReturn(Optional.of(role));

        Optional<Role> actual = roleService.getOneById(1L);

        assertTrue(actual.isPresent());
        assertEquals(role, actual.get());

        verify(roleRepository)
            .findById(1L);
    }
}
