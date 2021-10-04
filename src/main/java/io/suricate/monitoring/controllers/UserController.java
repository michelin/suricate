/*
 *
 *  * Copyright 2012-2021 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.suricate.monitoring.controllers;

import io.suricate.monitoring.configuration.swagger.ApiPageable;
import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.api.user.UserRequestDto;
import io.suricate.monitoring.model.dto.api.user.UserResponseDto;
import io.suricate.monitoring.model.dto.api.user.UserSettingRequestDto;
import io.suricate.monitoring.model.dto.api.user.UserSettingResponseDto;
import io.suricate.monitoring.model.entities.Setting;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.model.entities.UserSetting;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.suricate.monitoring.model.enums.AuthenticationMethod;
import io.suricate.monitoring.services.api.SettingService;
import io.suricate.monitoring.services.api.UserService;
import io.suricate.monitoring.services.api.UserSettingService;
import io.suricate.monitoring.services.mapper.UserMapper;
import io.suricate.monitoring.services.mapper.UserSettingMapper;
import io.suricate.monitoring.utils.exceptions.ApiException;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import io.swagger.annotations.*;
import org.apache.directory.api.ldap.aci.UserClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

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
     * The setting service
     */
    private final SettingService settingService;

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
                          final SettingService settingService,
                          final UserSettingService userSettingService,
                          final UserSettingMapper userSettingMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.settingService = settingService;
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
        @ApiResponse(code = 200, message = "Ok"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @ApiPageable
    @GetMapping(value = "/v1/users")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Page<UserResponseDto> getAll(@ApiParam(name = "search", value = "Search keyword")
                                        @RequestParam(value = "search", required = false) String search,
                                        Pageable pageable) {
        return userService.getAll(search, pageable)
                .map(userMapper::toUserDTO);
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
            .body(userMapper.toUserDTO(userOptional.get()));
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
            userRequestDto.getRoles()
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
     * @param userName The user name
     */
    @ApiOperation(value = "Retrieve the user settings", response = UserSettingResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = UserSettingResponseDto.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "User not found", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/users/{username}/settings")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<UserSettingResponseDto>> getUserSettings(@ApiParam(name = "userName", value = "The user name", required = true)
                                                                        @PathVariable("username") String username) {

        Optional<List<UserSetting>> userSettings = userSettingService.getUserSettingsByUsername(username);

        if (!userSettings.isPresent()) {
            throw new ObjectNotFoundException(UserSetting.class, username);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(userSettingMapper.toUserSettingsDTOs(userSettings.get()));
    }

    /**
     * Update the user settings for a user
     *
     * @param principal             The connected user
     * @param userName              The user name used in the url
     * @param userSettingRequestDto The new setting value
     * @return The user updated
     */
    @ApiOperation(value = "Update the user settings for a user")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Settings updated"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "User not found", response = ApiErrorDto.class)
    })
    @PutMapping(value = "/v1/users/{username}/settings/{settingId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserResponseDto> updateUserSettings(@ApiIgnore Principal principal,
                                                              @ApiParam(name = "userName", value = "The user name", required = true)
                                                              @PathVariable("username") String userName,
                                                              @ApiParam(name = "settingId", value = "The setting id", required = true)
                                                              @PathVariable("settingId") Long settingId,
                                                              @ApiParam(name = "userSettingRequestDto", value = "The new value of the setting", required = true)
                                                              @RequestBody UserSettingRequestDto userSettingRequestDto) {
        Optional<User> userOptional = this.userService.getOneByUsername(principal.getName());

        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException(User.class, userName);
        }

        if (!principal.getName().equals(userName)) {
            throw new AccessDeniedException(String.format("User %s is not allowed to modify this resource", principal.getName()));
        }

        Optional<Setting> settingOptional = this.settingService.getOneById(settingId);

        if (!settingOptional.isPresent()) {
            throw new ObjectNotFoundException(Setting.class, settingId);
        }

        userSettingService.updateUserSetting(userName, settingId, userSettingRequestDto);

        return ResponseEntity.noContent().build();
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
        User user = userMapper.toUserEntity(userRequestDto, AuthenticationMethod.DATABASE);
        Optional<User> userSavedOptional = userService.registerNewUserAccount(user);

        if (!userSavedOptional.isPresent()) {
            throw new ApiException(ApiErrorEnum.INTERNAL_SERVER_ERROR);
        }

        URI resourceLocation = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/api/users/" + userSavedOptional.get().getId())
            .build()
            .toUri();

        return ResponseEntity
            .created(resourceLocation)
            .contentType(MediaType.APPLICATION_JSON)
            .body(userMapper.toUserDTO(userSavedOptional.get()));
    }
}
