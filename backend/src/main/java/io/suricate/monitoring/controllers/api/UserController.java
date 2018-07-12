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

import io.suricate.monitoring.model.dto.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.setting.UserSettingDto;
import io.suricate.monitoring.model.dto.user.RoleDto;
import io.suricate.monitoring.model.dto.user.UserDto;
import io.suricate.monitoring.model.entity.user.User;
import io.suricate.monitoring.model.enums.AuthenticationMethod;
import io.suricate.monitoring.model.mapper.role.UserMapper;
import io.suricate.monitoring.service.api.UserService;
import io.suricate.monitoring.service.api.UserSettingService;
import io.suricate.monitoring.utils.exception.NoContentException;
import io.suricate.monitoring.utils.exception.ObjectNotFoundException;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import javax.transaction.Transactional;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User controller
 */
@RestController
@RequestMapping("/api/users")
@Api(value = "User Controller", tags = {"User"})
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
     * The user setting service
     */
    private final UserSettingService userSettingService;

    /**
     * The user mapper
     */
    private final UserMapper userMapper;

    /**
     * Constructor
     *
     * @param userService The user service to inject
     * @param userMapper  The user mapper to inject
     */
    @Autowired
    public UserController(final UserService userService,
                          final UserMapper userMapper,
                          final UserSettingService userSettingService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.userSettingService = userSettingService;
    }

    /**
     * List all user
     *
     * @return The list of all users
     */
    @ApiOperation(value = "Get the full list of users", response = UserDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = UserDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserDto>> getAll() {
        Optional<List<User>> users = userService.getAllOrderByUsername();

        if (!users.isPresent()) {
            throw new NoContentException(User.class);
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
    @ApiOperation(value = "Search a user by username", response = UserDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = UserDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<UserDto>> search(@ApiParam(name = "username", value = "The username to search", required = true)
                                                @RequestParam("username") String username) {
        Optional<List<User>> users = userService.getAllByUsernameStartWith(username);

        if (!users.isPresent()) {
            throw new NoContentException(User.class);
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
    @ApiOperation(value = "Register a new user", response = UserDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = UserDto.class),
        @ApiResponse(code = 400, message = "Bad request", response = ApiErrorDto.class),
    })
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<UserDto> register(@ApiParam(name = "userDto", value = "The user information to create", required = true)
                                            @RequestBody UserDto userDto) {
        User user = userMapper.toNewUser(userDto, AuthenticationMethod.DATABASE);
        Optional<User> userSaved = userService.registerNewUserAccount(user);

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
     * @param userId The user id to get
     * @return The user
     */
    @ApiOperation(value = "Get a user by id", response = UserDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = UserDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserDto> getOne(@ApiParam(name = "userId", value = "The user id", required = true)
                                          @PathVariable("userId") Long userId) {
        Optional<User> user = userService.getOne(userId);
        if (!user.isPresent()) {
            throw new ObjectNotFoundException(User.class, userId);
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
    @ApiOperation(value = "Delete a user by id", response = UserDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = UserDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<UserDto> deleteOne(@ApiParam(name = "userId", value = "The user id", required = true)
                                             @PathVariable("userId") Long userId) {
        Optional<User> userOptional = userService.getOne(userId);

        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException(User.class, userId);
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
     * @param userId  The user id
     * @param userDto The informations to update
     * @return The user updated
     */
    @ApiOperation(value = "Update a user by id", response = UserDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = UserDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "User not found", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserDto> updateOne(@ApiParam(name = "userId", value = "The user id", required = true)
                                             @PathVariable("userId") Long userId,
                                             @ApiParam(name = "userDto", value = "The user info to update", required = true)
                                             @RequestBody UserDto userDto) {
        Optional<User> userOptional = userService.updateUser(
            userId,
            userDto.getUsername(),
            userDto.getFirstname(),
            userDto.getLastname(),
            userDto.getEmail(),
            userDto.getRoles().stream().map(RoleDto::getName).collect(Collectors.toList())
        );

        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException(User.class, userId);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(userMapper.toUserDtoDefault(userOptional.get()));
    }

    /**
     * Update the user settings for a user
     *
     * @param principal       The connected user
     * @param userId          The user id used in the url
     * @param userSettingDtos The new settings
     * @return The user updated
     */
    @ApiOperation(value = "Update the user settings for a user", response = UserDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = UserDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "User not found", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/{userId}/settings", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserDto> updateUserSettings(@ApiIgnore Principal principal,
                                                      @ApiParam(name = "userId", value = "The user id", required = true)
                                                      @PathVariable("userId") Long userId,
                                                      @ApiParam(name = "userSettingDtos", value = "The list of user settings updated", required = true)
                                                      @RequestBody List<UserSettingDto> userSettingDtos) {

        Optional<User> userOptional = this.userService.getOneByUsername(principal.getName());
        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException(User.class, userId);
        }
        if (!userOptional.get().getId().equals(userId)) {
            throw new AccessDeniedException(String.format("User %s is not allowed to modify this resource", principal.getName()));
        }

        User user = userOptional.get();
        userSettingService.updateUserSettingsForUser(user, userSettingDtos);

        return ResponseEntity
            .ok()
            .cacheControl(CacheControl.noCache())
            .contentType(MediaType.APPLICATION_JSON)
            .body(userMapper.toUserDtoDefault(user));
    }

    /**
     * Get current user
     *
     * @param principal the user authenticated
     * @return The user informations
     */
    @ApiOperation(value = "Get the connected user", response = UserDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = UserDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "User not found", response = ApiErrorDto.class)
    })
    @RequestMapping(value = "/current", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserDto> getCurrentUser(@ApiIgnore Principal principal) {
        Optional<User> user = userService.getOneByUsername(principal.getName());

        if (!user.isPresent()) {
            throw new ObjectNotFoundException(User.class, principal.getName());
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .cacheControl(CacheControl.noCache())
            .body(userMapper.toUserDtoDefault(user.get()));

    }
}
