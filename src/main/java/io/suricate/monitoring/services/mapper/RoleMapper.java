/*
 * Copyright 2012-2018 the original author or authors.
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

package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.role.RoleResponseDto;
import io.suricate.monitoring.model.entity.user.Role;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for Role class
 */
@Mapper(componentModel = "spring")
public interface RoleMapper {

    /* ************************* TO DTO ********************************************** */

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a Role into a RoleResponseDto
     *
     * @param role The project to transform
     * @return The related role DTO
     */
    @Named("toRoleDtoDefault")
    RoleResponseDto toRoleDtoDefault(Role role);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a list of roles into a list of role dto
     *
     * @param roles The list of roles to transform
     * @return The related roles DTO
     */
    @Named("toRoleDtosDefault")
    @IterableMapping(qualifiedByName = "toRoleDtoDefault")
    List<RoleResponseDto> toRoleDtosDefault(List<Role> roles);
}
