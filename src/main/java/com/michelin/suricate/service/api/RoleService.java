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

import com.michelin.suricate.model.entity.Role;
import com.michelin.suricate.repository.RoleRepository;
import com.michelin.suricate.service.specification.RoleSearchSpecification;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/** Role service. */
@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Get a role by name.
     *
     * @param roleName The role name to find
     * @return The role as optional
     */
    public Optional<Role> getRoleByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    /**
     * Get the full list of roles.
     *
     * @return The roles
     */
    public Page<Role> getRoles(String search, Pageable pageable) {
        return roleRepository.findAll(new RoleSearchSpecification(search), pageable);
    }

    /**
     * Get a role by id.
     *
     * @param id The id of the role
     * @return The related role
     */
    public Optional<Role> getOneById(final Long id) {
        return roleRepository.findById(id);
    }
}
