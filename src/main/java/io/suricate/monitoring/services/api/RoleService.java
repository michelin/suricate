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

package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.entities.Role;
import io.suricate.monitoring.repositories.RoleRepository;
import io.suricate.monitoring.services.specifications.RoleSearchSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Get a role by name
     * @param roleName The role name to find
     * @return The role as optional
     */
    public Optional<Role> getRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    /**
     * Get the full list of roles
     * @return The roles
     */
    public Page<Role> getRoles(String search, Pageable pageable) {
        return roleRepository.findAll(new RoleSearchSpecification(search), pageable);
    }

    /**
     * Get a role by id
     * @param id The id of the role
     * @return The related role
     */
    public Optional<Role> getOneById(final Long id) {
        return roleRepository.findById(id);
    }
}
