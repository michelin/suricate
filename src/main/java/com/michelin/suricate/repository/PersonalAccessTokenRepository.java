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

import com.michelin.suricate.model.entity.PersonalAccessToken;
import com.michelin.suricate.model.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** Personal access token repository. */
@Repository
public interface PersonalAccessTokenRepository
        extends CrudRepository<PersonalAccessToken, Long>, JpaSpecificationExecutor<PersonalAccessToken> {
    /**
     * Find all tokens of given user.
     *
     * @param user The user
     * @return The user tokens
     */
    List<PersonalAccessToken> findAllByUser(User user);

    /**
     * Find a token by given name and user.
     *
     * @param name The token name
     * @param user The user
     * @return The token
     */
    Optional<PersonalAccessToken> findByNameAndUser(String name, User user);

    /**
     * Find a token by given checksum.
     *
     * @param checksum The token checksum
     * @return The token
     */
    @EntityGraph(attributePaths = "user.roles")
    Optional<PersonalAccessToken> findByChecksum(Long checksum);
}
