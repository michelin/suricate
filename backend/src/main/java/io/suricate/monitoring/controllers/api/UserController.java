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

package io.suricate.monitoring.controllers.api;

import io.suricate.monitoring.controllers.api.error.exception.ApiException;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.suricate.monitoring.model.dto.user.UserDto;
import io.suricate.monitoring.model.entity.user.User;
import io.suricate.monitoring.model.enums.AuthenticationMethod;
import io.suricate.monitoring.service.api.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User controller
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /**
     * Class logger
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    /**
     * The user service
     */
    private final UserService userService;

    /**
     * Constructor
     *
     * @param userService The user service to inject
     */
    @Autowired
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    /**
     * List all user
     * @return The list of all users
     */
    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserDto> getAll() {
        List<User> users =  userService.getAll();
        return users.stream().map(user -> userService.toDto(user)).collect(Collectors.toList());
    }

    /**
     * Search users by username
     *
     * @param username The username query
     * @return The user that match with the query
     */
    @RequestMapping(value="/search", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<UserDto> search(@RequestParam("username") String username) {
        Optional<List<User>> users = userService.getAllByUsernameStartWith(username);
        if(!users.isPresent()) {
            return new ArrayList<>();
        }

        return users.get().stream().map(user -> userService.toDto(user)).collect(Collectors.toList());
    }

    /**
     * Register a new user in the database
     *
     * @param userDto The user to register
     * @return The user registered
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @PreAuthorize("isAnonymous()")
    public UserDto register(@RequestBody UserDto userDto) {
        User userSaved = userService.registerNewUserAccount(userDto, AuthenticationMethod.DATABASE);
        return userService.toDto(userSaved);
    }

    /**
     * List a specific user
     *
     * @param id The user id to get
     * @return The user
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public UserDto getOne(@PathVariable("id") Long id) {
        Optional<User> user = userService.getOne(id);
        if (!user.isPresent()){
            throw new ApiException(ApiErrorEnum.USER_NOT_FOUND);
        }

        return userService.toDto(user.get());
    }

    /**
     * Get current user
     *
     * @param principal the user authenticated
     * @return The user informations
     */
    @RequestMapping(value = "/current", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public UserDto getCurrentUser(Principal principal) {
        Optional<User> user = userService.getOneByUsername(principal.getName());

        if(!user.isPresent()) {
            throw new ApiException(ApiErrorEnum.USER_NOT_FOUND);
        }

        return userService.toDto(user.get());
    }
}
