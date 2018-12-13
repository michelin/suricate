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

import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.api.role.RoleResponseDto;
import io.suricate.monitoring.model.dto.api.user.UserRequestDto;
import io.suricate.monitoring.model.dto.api.user.UserResponseDto;
import io.suricate.monitoring.model.dto.api.user.UserSettingResponseDto;
import io.suricate.monitoring.model.entity.user.User;
import io.suricate.monitoring.model.enums.AuthenticationMethod;
import io.suricate.monitoring.service.api.UserService;
import io.suricate.monitoring.service.api.UserSettingService;
import io.suricate.monitoring.service.mapper.UserMapper;
import io.suricate.monitoring.service.mapper.UserSettingMapper;
import io.suricate.monitoring.utils.exception.NoContentException;
import io.suricate.monitoring.utils.exception.ObjectNotFoundException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api")
@Api(value = "User Controller", tags = {"Users"})
public class UserController {

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
     * The user setting mapper
     */
    private final UserSettingMapper userSettingMapper;

    /**
     * Constructor
     *
     * @param userService       The user service to inject
     * @param userMapper        The user mapper to inject
     * @param userSettingMapper The user setting mapper
     */
    @Autowired
    public UserController(final UserService userService,
                          final UserMapper userMapper,
                          final UserSettingService userSettingService,
                          final UserSettingMapper userSettingMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.userSettingService = userSettingService;
        this.userSettingMapper = userSettingMapper;
    }

    /**
     * List all user
     *
     * @return The list of all users
     */
    @ApiOperation(value = "Get the full list of users", response = UserResponseDto.class, nickname = "getAllUsers")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = UserResponseDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAll(@RequestParam(value = "filter", required = false, defaultValue = "") String filter) {
        Optional<List<User>> usersOptional = userService.getAllByUsernameStartWith(filter);
        if (!usersOptional.isPresent()) {
            throw new NoContentException(User.class);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(userMapper.toUserDtosDefault(usersOptional.get()));
    }

    /**
     * List a specific user
     *
     * @param userId The user id to get
     * @return The user
     */
    @ApiOperation(value = "Get a user by id", response = UserResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = UserResponseDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/users/{userId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserResponseDto> getOne(@ApiParam(name = "userId", value = "The user id", required = true)
                                                  @PathVariable("userId") Long userId) {
        Optional<User> userOptional = userService.getOne(userId);
        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException(User.class, userId);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(userMapper.toUserDtoDefault(userOptional.get()));
    }

    /**
     * Update a user
     *
     * @param userId         The user id
     * @param userRequestDto The information to update
     * @return The user updated
     */
    @ApiOperation(value = "Update a user by id")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "User updated"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "User not found", response = ApiErrorDto.class)
    })
    @PutMapping(value = "/v1/users/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateOne(@ApiParam(name = "userId", value = "The user id", required = true)
                                          @PathVariable("userId") Long userId,
                                          @ApiParam(name = "userResponseDto", value = "The user info to update", required = true)
                                          @RequestBody UserRequestDto userRequestDto) {
        Optional<User> userOptional = userService.updateUser(
            userId,
            userRequestDto.getUsername(),
            userRequestDto.getFirstname(),
            userRequestDto.getLastname(),
            userRequestDto.getEmail(),
            userRequestDto.getRoles().stream().map(RoleResponseDto::getName).collect(Collectors.toList())
        );

        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException(User.class, userId);
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * Delete a user
     *
     * @param userId The user id to delete
     * @return The user deleted
     */
    @ApiOperation(value = "Delete a user by id")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "User deleted"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @DeleteMapping(value = "/v1/users/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<Void> deleteOne(@ApiParam(name = "userId", value = "The user id", required = true)
                                          @PathVariable("userId") Long userId) {
        Optional<User> userOptional = userService.getOne(userId);
        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException(User.class, userId);
        }

        userService.deleteUserByUserId(userOptional.get());
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieve the user settings
     *
     * @param userId The user id used in the url
     */
    @ApiOperation(value = "Retrieve the user settings", response = UserSettingResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = UserSettingResponseDto.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "User not found", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/users/{userId}/settings")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<UserSettingResponseDto>> getUserSettings(@ApiParam(name = "userId", value = "The user id", required = true)
                                                                        @PathVariable("userId") Long userId) {
        Optional<User> userOptional = userService.getOne(userId);
        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException(User.class, userId);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(userSettingMapper.toUserSettingDtosDefault(userOptional.get().getUserSettings()));
    }

    /**
     * Update the user settings for a user
     *
     * @param principal               The connected user
     * @param userId                  The user id used in the url
     * @param userSettingResponseDtos The new settings
     * @return The user updated
     */
    @ApiOperation(value = "Update the user settings for a user")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Settings updated"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "User not found", response = ApiErrorDto.class)
    })
    @PutMapping(value = "/v1/users/{userId}/settings")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserResponseDto> updateUserSettings(@ApiIgnore Principal principal,
                                                              @ApiParam(name = "userId", value = "The user id", required = true)
                                                              @PathVariable("userId") Long userId,
                                                              @ApiParam(name = "userSettingResponseDtos", value = "The list of user settings updated", required = true)
                                                              @RequestBody List<UserSettingResponseDto> userSettingResponseDtos) {
        Optional<User> userOptional = this.userService.getOneByUsername(principal.getName());
        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException(User.class, userId);
        }
        if (!userOptional.get().getId().equals(userId)) {
            throw new AccessDeniedException(String.format("User %s is not allowed to modify this resource", principal.getName()));
        }

        userSettingService.updateUserSettingsForUser(userOptional.get(), userSettingResponseDtos);

        return ResponseEntity.noContent().build();
    }

    /**
     * Get current user
     *
     * @param principal the user authenticated
     * @return The user informations
     */
    @ApiOperation(value = "Get the connected user", response = UserResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = UserResponseDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "User not found", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/users/current")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserResponseDto> getCurrentUser(@ApiIgnore Principal principal) {
        Optional<User> userOptional = userService.getOneByUsername(principal.getName());
        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException(User.class, principal.getName());
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(userMapper.toUserDtoDefault(userOptional.get()));
    }

    /**
     * Register a new user in the database
     *
     * @param userRequestDto The user to register
     * @return The user registered
     */
    @ApiOperation(value = "Register a new user", response = UserResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = UserResponseDto.class),
        @ApiResponse(code = 400, message = "Bad request", response = ApiErrorDto.class),
    })
    @PostMapping(value = "/v1/users/register")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<UserResponseDto> register(@ApiParam(name = "userResponseDto", value = "The user information to create", required = true)
                                                    @RequestBody UserRequestDto userRequestDto) {
        User user = userMapper.toNewUser(userRequestDto, AuthenticationMethod.DATABASE);
        Optional<User> userSavedOptional = userService.registerNewUserAccount(user);

        URI resourceLocation = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/api/users/" + userSavedOptional.get().getId())
            .build()
            .toUri();

        return ResponseEntity
            .created(resourceLocation)
            .contentType(MediaType.APPLICATION_JSON)
            .body(userMapper.toUserDtoDefault(userSavedOptional.get()));
    }
}
