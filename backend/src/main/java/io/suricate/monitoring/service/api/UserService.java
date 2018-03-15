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

import io.suricate.monitoring.configuration.security.ConnectedUser;
import io.suricate.monitoring.controllers.api.error.exception.ApiException;
import io.suricate.monitoring.model.dto.user.UserDto;
import io.suricate.monitoring.model.entity.project.Project;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.suricate.monitoring.model.enums.AuthenticationMethod;
import io.suricate.monitoring.model.enums.UserRoleEnum;
import io.suricate.monitoring.model.entity.user.Role;
import io.suricate.monitoring.model.entity.user.User;
import io.suricate.monitoring.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
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

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(final UserRepository userRepository, final RoleService roleService, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto toDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.getRoles().addAll(user.getRoles().stream().map(role -> roleService.toDto(role)).collect(Collectors.toList()));
        userDto.setUsername(user.getUsername());
        userDto.setFirstname(user.getFirstname());
        userDto.setLastname(user.getLastname());
        userDto.setFullname(user.getFirstname() + " " + user.getLastname());
        userDto.setEmail(user.getEmail());
        userDto.setAuthenticationMethod(user.getAuthenticationMethod());

        return userDto;
    }

    public User toModel(UserDto userDto) {
        User user = new User();

        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setFirstname(userDto.getFirstname());
        user.setLastname(userDto.getLastname());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setAuthenticationMethod(userDto.getAuthenticationMethod());
        user.getRoles().addAll( userDto.getRoles().stream().map(roleDto -> roleService.toModel(roleDto)).collect(Collectors.toList()) );

        return user;
    }

    public User registerNewUserAccount(UserDto userDto) {
        userDto.setAuthenticationMethod(AuthenticationMethod.DATABASE);
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userDto.setConfirmPassword(passwordEncoder.encode(userDto.getConfirmPassword()));

        Optional<Role> role = roleService.getRoleByName(UserRoleEnum.ROLE_USER.name());
        if(!role.isPresent()) {
            LOGGER.debug("Cannot find Role");
        }
        userDto.setRoles(Arrays.asList(roleService.toDto(role.get())));

        User user = toModel(userDto);
        userRepository.save(user);

        return user;
    }

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

        Optional<Role> role;
        if(userRepository.count() > 0) {
            role = roleService.getRoleByName(UserRoleEnum.ROLE_USER.name());
        } else {
            role = roleService.getRoleByName(UserRoleEnum.ROLE_ADMIN.name());
        }

        if (!role.isPresent()) {
            LOGGER.error("Role {} not available in database", UserRoleEnum.ROLE_USER);
            throw new ApiException(ApiErrorEnum.DATABASE_INIT_ISSUE);
        }

        user.getRoles().add(role.get());
        userRepository.save(user);  // Save user

        return Optional.of(user);
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public Optional<User> getOne(Long userId) {
        User user = userRepository.findOne(userId);
        if(user == null) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    public Optional<User> getOneByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    public Optional<List<User>> getAllByUsernameStartWith(String username) {
        return userRepository.findByUsernameIgnoreCaseAndStartingWith(username);
    }

    public Optional<List<User>> getAllByProject(Project project) {
        return userRepository.findByProjects_Id(project.getId());
    }
}
