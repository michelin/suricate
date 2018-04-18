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

package io.suricate.monitoring.service.api;

import io.suricate.monitoring.model.dto.user.RoleDto;
import io.suricate.monitoring.model.entity.user.Role;
import io.suricate.monitoring.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Role service
 */
@Service
public class RoleService {
    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleService.class);

    /**
     * Role repository
     */
    private final RoleRepository roleRepository;

    /**
     * Constructor
     *
     * @param roleRepository inject the role repository
     */
    public RoleService(final RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Tranform a Domain object into a DTO object
     * @param role The domain role
     * @return The DTO Object
     */
    public RoleDto toDto(Role role) {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(role.getId());
        roleDto.setName(role.getName());
        roleDto.setDescription(role.getDescription());

        return roleDto;
    }

    /**
     * Tranform a DTO object into a domain object
     *
     * @param roleDto The DTO to transform
     * @return The domain object
     */
    public Role toModel(RoleDto roleDto) {
        Role role = new Role();
        role.setId(roleDto.getId());
        role.setName(roleDto.getName());
        role.setDescription(roleDto.getDescription());

        return role;
    }

    /**
     * Get a role by name
     *
     * @param roleName The role name to find
     * @return The role as optional
     */
    public Optional<Role> getRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }
}
