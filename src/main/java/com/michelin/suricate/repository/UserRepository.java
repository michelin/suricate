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
package com.michelin.suricate.repository;

import com.michelin.suricate.model.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** User repository. */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    /**
     * Find all paginated users.
     *
     * @param specification The specification to apply
     * @param pageable The pageable to apply
     * @return The paginated users
     */
    @EntityGraph(attributePaths = "roles")
    Page<User> findAll(Specification<User> specification, Pageable pageable);

    /**
     * Find a user by the username ignoring case.
     *
     * @param username The username
     * @return The user as optional
     */
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByUsernameIgnoreCase(String username);

    /**
     * Find a user by the email ignoring case.
     *
     * @param email The email
     * @return The user as optional
     */
    @EntityGraph(attributePaths = "roles")
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Check if a given username exists.
     *
     * @param username The username
     * @return true if it is, false otherwise
     */
    boolean existsByUsername(String username);
}
