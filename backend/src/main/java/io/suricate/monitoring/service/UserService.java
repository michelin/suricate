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

package io.suricate.monitoring.service;

import io.suricate.monitoring.config.security.ConnectedUser;
import io.suricate.monitoring.controllers.api.error.exception.ApiException;
import io.suricate.monitoring.model.dto.user.UserDto;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.suricate.monitoring.model.enums.AuthenticationMethod;
import io.suricate.monitoring.model.enums.UserRoleEnum;
import io.suricate.monitoring.model.user.Role;
import io.suricate.monitoring.model.user.User;
import io.suricate.monitoring.repository.RoleRepository;
import io.suricate.monitoring.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service used to manage user
 */
@Service
public class UserService {

    /** Class logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public Optional<User> initUser(ConnectedUser connectedUser){

        if (connectedUser == null){
            return Optional.empty();
        }
        // Create user
        User user = new User();
        user.setFirstname(connectedUser.getFirstname());
        user.setLastname(connectedUser.getLastname());
        user.setUsername(connectedUser.getUsername());
        user.setEmail(connectedUser.getMail());
        user.setAuthenticationMethod(AuthenticationMethod.LDAP);

        Optional<Role> role = roleRepository.findByName(UserRoleEnum.ROLE_USER.name());
        if (!role.isPresent()) {
            LOGGER.error("Role {} not available in database", UserRoleEnum.ROLE_USER);
            throw new ApiException(ApiErrorEnum.DATABASE_INIT_ISSUE);
        }

        user.getRoles().add(role.get());
        userRepository.save(user);  // Save user

        return Optional.of(user);
    }

    public List<UserDto> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> new UserDto(user)).collect(Collectors.toList());
    }

    public UserDto getOne(Long userId) {
        User user = userRepository.findOne(userId);
        return new UserDto(user);
    }

    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Long getIdByUsername(String username) {
        return userRepository.getIdByUsername(username);
    }

    @Transactional
    public User saveUserToken(Long userId, String token) {
        User user = userRepository.findOne(userId);
        user.setToken(token);

        return userRepository.save(user);
    }
}
