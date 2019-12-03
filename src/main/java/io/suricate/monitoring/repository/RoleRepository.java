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

package io.suricate.monitoring.repository;

import io.suricate.monitoring.model.entity.user.Role;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository used for request Roles in database
 */
public interface RoleRepository extends CrudRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    /**
     * Find a role by name
     *
     * @param name The name of the role
     * @return The role as optional
     */
    Optional<Role> findByName(String name);

    /**
     * Find the list of roles for a user
     *
     * @param id The user id
     * @return The list of related roles
     */
    List<Role> findByUsers_Id(Long id);
}
