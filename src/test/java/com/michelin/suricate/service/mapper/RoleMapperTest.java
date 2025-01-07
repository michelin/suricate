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
