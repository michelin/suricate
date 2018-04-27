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
import io.suricate.monitoring.model.mapper.role.UserMapper;
import io.suricate.monitoring.service.api.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.swing.text.html.Option;
import java.net.URI;
import java.security.Principal;
import java.util.*;

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
     * The user mapper
     */
    private final UserMapper userMapper;

    /**
     * Constructor
     *
     * @param userService The user service to inject
     * @param userMapper The user mapper to inject
     */
    @Autowired
    public UserController(final UserService userService, final UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * List all user
     * @return The list of all users
     */
    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserDto>> getAll() {
        Optional<List<User>> users =  userService.getAllOrderByUsername();

        if(!users.isPresent()) {
            return ResponseEntity
                .noContent()
                .cacheControl(CacheControl.noCache())
                .build();
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(userMapper.toUserDtosDefault(users.get()));
    }

    /**
     * Search users by username
     *
     * @param username The username query
     * @return The user that match with the query
     */
    @RequestMapping(value="/search", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<UserDto>> search(@RequestParam("username") String username) {
        Optional<List<User>> users = userService.getAllByUsernameStartWith(username);

        if(!users.isPresent()) {
            return ResponseEntity
                .noContent()
                .cacheControl(CacheControl.noCache())
                .build();
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(userMapper.toUserDtosDefault(users.get()));
    }

    /**
     * Register a new user in the database
     *
     * @param userDto The user to register
     * @return The user registered
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<UserDto> register(@RequestBody UserDto userDto) {
        User user = userMapper.toNewUser(userDto, AuthenticationMethod.DATABASE);
        Optional<User> userSaved = userService.registerNewUserAccount(user);

        if(!userSaved.isPresent()) {
            throw new ApiException(ApiErrorEnum.USER_CREATION_ERROR);
        }

        URI resourceLocation = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/api/users/" + user.getId())
            .build()
            .toUri();

        return ResponseEntity
            .created(resourceLocation)
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(userMapper.toUserDtoDefault(userSaved.get()));
    }

    /**
     * List a specific user
     *
     * @param id The user id to get
     * @return The user
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserDto> getOne(@PathVariable("id") Long id) {
        Optional<User> user = userService.getOne(id);
        if (!user.isPresent()){
            throw new ApiException(ApiErrorEnum.USER_NOT_FOUND);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(userMapper.toUserDtoDefault(user.get()));
    }

    /**
     * Delete a user
     *
     * @param userId The user id to delete
     * @return The user deleted
     */
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserDto> deleteOne(@PathVariable("userId") Long userId) {
        Optional<User> userOptional = userService.getOne(userId);

        if(!userOptional.isPresent()) {
            return ResponseEntity
                    .notFound()
                    .cacheControl(CacheControl.noCache())
                    .build();
        }

        userService.deleteUserByUserId(userOptional.get());
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .cacheControl(CacheControl.noCache())
                .body(userMapper.toUserDtoDefault(userOptional.get()));
    }

    /**
     * Update a user
     *
     * @param userId The user id
     * @param userDto The informations to update
     * @return The user updated
     */
    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserDto> updateOne(@PathVariable("userId") Long userId, @RequestBody UserDto userDto) {
        Optional<User> userOptional = userService.updateUser(
            userId,
            userDto.getUsername(),
            userDto.getFirstname(),
            userDto.getLastname(),
            userDto.getEmail()
        );

        if(!userOptional.isPresent()) {
            return ResponseEntity
                .notFound()
                .cacheControl(CacheControl.noCache())
                .build();
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(userMapper.toUserDtoDefault(userOptional.get()));
    }

    /**
     * Get current user
     *
     * @param principal the user authenticated
     * @return The user informations
     */
    @RequestMapping(value = "/current", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal) {
        Optional<User> user = userService.getOneByUsername(principal.getName());

        if(!user.isPresent()) {
            throw new ApiException(ApiErrorEnum.USER_NOT_FOUND);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(userMapper.toUserDtoDefault(user.get()));

    }
}
