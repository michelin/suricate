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

import com.michelin.suricate.model.entity.PersonalAccessToken;
import com.michelin.suricate.model.entity.User;
import com.michelin.suricate.repository.PersonalAccessTokenRepository;
import com.michelin.suricate.security.LocalUser;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Personal access token service.
 */
@Service
public class PersonalAccessTokenService {
    @Autowired
    private PersonalAccessTokenRepository personalAccessTokenRepository;

    /**
     * Get all user tokens.
     *
     * @param user The user
     * @return The user tokens
     */
    @Transactional(readOnly = true)
    public List<PersonalAccessToken> findAllByUser(User user) {
        return personalAccessTokenRepository.findAllByUser(user);
    }

    /**
     * Find a token by given name and user.
     *
     * @param name The token name
     * @param user The user
     * @return The token
     */
    @Transactional(readOnly = true)
    public Optional<PersonalAccessToken> findByNameAndUser(String name, User user) {
        return personalAccessTokenRepository.findByNameAndUser(name, user);
    }

    /**
     * Find a token by given checksum.
     *
     * @param checksum The token checksum
     * @return The token
     */
    @Transactional(readOnly = true)
    public Optional<PersonalAccessToken> findByChecksum(Long checksum) {
        return personalAccessTokenRepository.findByChecksum(checksum);
    }

    /**
     * Create a JWT token.
     *
     * @param tokenName     The token name
     * @param checksum      The token checksum
     * @param connectedUser The authenticated user
     */
    @Transactional
    public PersonalAccessToken create(String tokenName, Long checksum, LocalUser connectedUser) {
        PersonalAccessToken personalAccessToken = new PersonalAccessToken();
        personalAccessToken.setName(tokenName);
        personalAccessToken.setChecksum(checksum);
        personalAccessToken.setUser(connectedUser.getUser());

        return personalAccessTokenRepository.save(personalAccessToken);
    }

    /**
     * Delete a token by id.
     *
     * @param id The token id
     */
    public void deleteById(Long id) {
        personalAccessTokenRepository.deleteById(id);
    }
}
