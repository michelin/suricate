/*
 * Copyright 2012-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.michelin.suricate.services.mapper;

import com.michelin.suricate.model.dto.api.role.RoleResponseDto;
import com.michelin.suricate.model.entities.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

/**
 * Role mapper.
 */
@Mapper(componentModel = "spring")
public abstract class RoleMapper {
    /**
     * Map a role into a DTO.
     *
     * @param role The project to map
     * @return The role as DTO
     */
    @Named("toRoleDto")
    public abstract RoleResponseDto toRoleDto(Role role);
}
