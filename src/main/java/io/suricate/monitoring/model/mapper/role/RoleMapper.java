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

package io.suricate.monitoring.model.mapper.role;

import io.suricate.monitoring.model.dto.api.user.RoleDto;
import io.suricate.monitoring.model.entity.user.Role;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for Role class
 */
@Component
@Mapper(
    componentModel = "spring",
    uses = {
        UserMapper.class
    }
)
public abstract class RoleMapper {

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a Role into a RoleDto
     *
     * @param role The project to transform
     * @return The related role DTO
     */
    @Named("toRoleDtoDefault")
    @Mappings({
        @Mapping(target = "users", qualifiedByName = "toUserDtosWithoutRole")
    })
    public abstract RoleDto toRoleDtoDefault(Role role);

    /**
     * Will be used by "toUserDto" prevent cycle references on User -> Roles -> user
     * @param role The role to tranform
     * @return The role without users
     */
    @Named("toRoleDtoWithoutUsers")
    @Mappings({
        @Mapping(target = "users", ignore = true)
    })
    public abstract RoleDto toRoleDtoWithoutUsers(Role role);

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
    public abstract List<RoleDto> toRoleDtosDefault(List<Role> roles);

    /**
     * Tranform a list of roles into a list of role dto without user
     *
     * @param roles The list of roles to transform
     * @return The related roles DTO
     */
    @Named("toRoleDtosWithoutUsers")
    @IterableMapping(qualifiedByName = "toRoleDtoWithoutUsers")
    public abstract List<RoleDto> toRoleDtosWithoutUsers(List<Role> roles);
}
