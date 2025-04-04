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
package com.michelin.suricate.controller;

import com.michelin.suricate.model.dto.api.error.ApiErrorDto;
import com.michelin.suricate.model.dto.api.token.PersonalAccessTokenRequestDto;
import com.michelin.suricate.model.dto.api.token.PersonalAccessTokenResponseDto;
import com.michelin.suricate.model.dto.api.user.AdminUserResponseDto;
import com.michelin.suricate.model.dto.api.user.UserRequestDto;
import com.michelin.suricate.model.dto.api.user.UserResponseDto;
import com.michelin.suricate.model.dto.api.user.UserSettingRequestDto;
import com.michelin.suricate.model.dto.api.user.UserSettingResponseDto;
import com.michelin.suricate.model.entity.PersonalAccessToken;
import com.michelin.suricate.model.entity.Setting;
import com.michelin.suricate.model.entity.User;
import com.michelin.suricate.model.entity.UserSetting;
import com.michelin.suricate.model.enumeration.AuthenticationProvider;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.service.api.PersonalAccessTokenService;
import com.michelin.suricate.service.api.SettingService;
import com.michelin.suricate.service.api.UserService;
import com.michelin.suricate.service.api.UserSettingService;
import com.michelin.suricate.service.mapper.PersonalAccessTokenMapper;
import com.michelin.suricate.service.mapper.UserMapper;
import com.michelin.suricate.service.mapper.UserSettingMapper;
import com.michelin.suricate.service.token.PersonalAccessTokenHelperService;
import com.michelin.suricate.util.exception.EmailAlreadyExistException;
import com.michelin.suricate.util.exception.ObjectNotFoundException;
import com.michelin.suricate.util.exception.UsernameAlreadyExistException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/** User controller. */
@RestController
@RequestMapping("/api")
@Tag(name = "User", description = "User Controller")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserSettingService userSettingService;

    @Autowired
    private SettingService settingService;

    @Autowired
    private PersonalAccessTokenHelperService patHelperService;

    @Autowired
    private PersonalAccessTokenService patService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserSettingMapper userSettingMapper;

    @Autowired
    private PersonalAccessTokenMapper personalAccessTokenMapper;

    /**
     * List all users for admins.
     *
     * @return The list of all users
     */
    @Operation(summary = "Get the full list of users")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "204", description = "No Content"),
                @ApiResponse(
                        responseCode = "401",
                        description = "Authentication error, token expired or invalid",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
                @ApiResponse(
                        responseCode = "403",
                        description = "You don't have permission to access to this resource",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
            })
    @PageableAsQueryParam
    @GetMapping(value = "/v1/admin/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<AdminUserResponseDto> getAllForAdmins(
            @Parameter(name = "search", description = "Search keyword")
                    @RequestParam(value = "search", required = false)
                    String search,
            @Parameter(hidden = true) Pageable pageable) {
        return userService.getAll(search, pageable).map(userMapper::toAdminUserDto);
    }

    /**
     * List all users.
     *
     * @return The list of all users
     */
    @Operation(summary = "Get the full list of users")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "204", description = "No Content"),
                @ApiResponse(
                        responseCode = "401",
                        description = "Authentication error, token expired or invalid",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
                @ApiResponse(
                        responseCode = "403",
                        description = "You don't have permission to access to this resource",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
            })
    @PageableAsQueryParam
    @GetMapping(value = "/v1/users")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Page<UserResponseDto> getAll(
            @Parameter(name = "search", description = "Search keyword")
                    @RequestParam(value = "search", required = false)
                    String search,
            @Parameter(hidden = true) Pageable pageable) {
        return userService.getAll(search, pageable).map(userMapper::toUserDto);
    }

    /**
     * Get a specific user.
     *
     * @param userId The user id to get
     * @return The user
     */
    @Operation(summary = "Get a user by id")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(
                        responseCode = "401",
                        description = "Authentication error, token expired or invalid",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
                @ApiResponse(
                        responseCode = "403",
                        description = "You don't have permission to access to this resource",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
            })
    @GetMapping(value = "/v1/users/{userId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserResponseDto> getOne(
            @Parameter(name = "userId", description = "The user id", required = true, example = "1")
                    @PathVariable("userId")
                    Long userId) {
        Optional<User> userOptional = userService.getOne(userId);
        if (userOptional.isEmpty()) {
            throw new ObjectNotFoundException(User.class, userId);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userMapper.toUserDto(userOptional.get()));
    }

    /**
     * Update a user.
     *
     * @param userId The user id
     * @param userRequestDto The information to update
     * @return The user updated
     */
    @Operation(summary = "Update a user by id")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "User updated"),
                @ApiResponse(
                        responseCode = "401",
                        description = "Authentication error, token expired or invalid",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
                @ApiResponse(
                        responseCode = "403",
                        description = "You don't have permission to access to this resource",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
            })
    @PutMapping(value = "/v1/users/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> updateOne(
            @Parameter(name = "userId", description = "The user id", required = true, example = "1")
                    @PathVariable("userId")
                    Long userId,
            @Parameter(name = "userResponseDto", description = "The user info to update", required = true) @RequestBody
                    UserRequestDto userRequestDto) {
        Optional<User> userOptional = userService.updateUser(
                userId,
                userRequestDto.getUsername(),
                userRequestDto.getFirstname(),
                userRequestDto.getLastname(),
                userRequestDto.getEmail(),
                userRequestDto.getRoles());

        if (userOptional.isEmpty()) {
            throw new ObjectNotFoundException(User.class, userId);
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * Delete a user.
     *
     * @param userId The user id to delete
     * @return The user deleted
     */
    @Operation(summary = "Delete a user by id")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "User deleted"),
                @ApiResponse(
                        responseCode = "401",
                        description = "Authentication error, token expired or invalid",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
                @ApiResponse(
                        responseCode = "403",
                        description = "You don't have permission to access to this resource",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
            })
    @DeleteMapping(value = "/v1/users/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public ResponseEntity<Void> deleteOne(
            @Parameter(name = "userId", description = "The user id", required = true, example = "1")
                    @PathVariable("userId")
                    Long userId) {
        Optional<User> userOptional = userService.getOne(userId);
        if (userOptional.isEmpty()) {
            throw new ObjectNotFoundException(User.class, userId);
        }

        userService.deleteUserByUserId(userOptional.get());
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieve the user settings.
     *
     * @param username The username
     */
    @Operation(summary = "Retrieve the user settings")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(
                        responseCode = "401",
                        description = "Authentication error, token expired or invalid",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
                @ApiResponse(
                        responseCode = "403",
                        description = "You don't have permission to access to this resource",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
            })
    @GetMapping(value = "/v1/users/{username}/settings")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<UserSettingResponseDto>> getUserSettings(
            @Parameter(name = "userName", description = "The user name", required = true) @PathVariable("username")
                    String username) {
        Optional<List<UserSetting>> userSettings = userSettingService.getUserSettingsByUsername(username);

        if (userSettings.isEmpty()) {
            throw new ObjectNotFoundException(UserSetting.class, username);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userSettingMapper.toUserSettingsDtos(userSettings.get()));
    }

    /**
     * Update the user settings for a user.
     *
     * @param connectedUser The connected user
     * @param userName The username used in the url
     * @param userSettingRequestDto The new setting value
     * @return The user updated
     */
    @Operation(summary = "Update the user settings for a user")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Settings updated"),
                @ApiResponse(
                        responseCode = "401",
                        description = "Authentication error, token expired or invalid",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
                @ApiResponse(
                        responseCode = "403",
                        description = "You don't have permission to access to this resource",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
                @ApiResponse(
                        responseCode = "404",
                        description = "User not found",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
            })
    @PutMapping(value = "/v1/users/{username}/settings/{settingId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserResponseDto> updateUserSettings(
            @Parameter(hidden = true) @AuthenticationPrincipal LocalUser connectedUser,
            @Parameter(name = "userName", description = "The user name", required = true) @PathVariable("username")
                    String userName,
            @Parameter(name = "settingId", description = "The setting id", required = true, example = "1")
                    @PathVariable("settingId")
                    Long settingId,
            @Parameter(name = "userSettingRequestDto", description = "The new value of the setting", required = true)
                    @RequestBody
                    UserSettingRequestDto userSettingRequestDto) {
        Optional<User> userOptional = userService.getOneByUsername(connectedUser.getUsername());

        if (userOptional.isEmpty()) {
            throw new ObjectNotFoundException(User.class, userName);
        }

        if (!connectedUser.getUsername().equals(userName)) {
            throw new AccessDeniedException(
                    String.format("User %s is not allowed to modify this resource", connectedUser.getUsername()));
        }

        Optional<Setting> settingOptional = settingService.getOneById(settingId);

        if (settingOptional.isEmpty()) {
            throw new ObjectNotFoundException(Setting.class, settingId);
        }

        userSettingService.updateUserSetting(userName, settingId, userSettingRequestDto);

        return ResponseEntity.noContent().build();
    }

    /**
     * Register a new user in the database.
     *
     * @param userRequestDto The user to register
     * @return The user registered
     */
    @Operation(summary = "Register a new user")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad request",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
                @ApiResponse(
                        responseCode = "409",
                        description = "Username or email already taken",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
            })
    @PostMapping(value = "/v1/users/signup")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<UserResponseDto> signUp(
            @Parameter(name = "userResponseDto", description = "The user information to create", required = true)
                    @RequestBody
                    UserRequestDto userRequestDto) {
        if (userService.getOneByUsername(userRequestDto.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistException(userRequestDto.getUsername());
        }

        if (userService.getOneByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistException(userRequestDto.getEmail());
        }

        User user = userMapper.toUserEntity(userRequestDto, AuthenticationProvider.DATABASE);
        User savedUser = userService.create(user);

        URI resourceLocation = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/users/" + savedUser.getId())
                .build()
                .toUri();

        return ResponseEntity.created(resourceLocation)
                .contentType(MediaType.APPLICATION_JSON)
                .body(userMapper.toUserDto(savedUser));
    }

    /**
     * Get all user personal access tokens.
     *
     * @param connectedUser The authentication principal as LocalUser
     * @return A list of personal access tokens
     */
    @Operation(summary = "Get all user personal access tokens")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(
                        responseCode = "401",
                        description = "Authentication error, token expired or invalid",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
            })
    @GetMapping(value = "/v1/users/personal-access-token")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<PersonalAccessTokenResponseDto>> getPersonalAccessTokens(
            @Parameter(hidden = true) @AuthenticationPrincipal LocalUser connectedUser) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(personalAccessTokenMapper.toPersonalAccessTokensDtos(
                        patService.findAllByUser(connectedUser.getUser())));
    }

    /**
     * Generate a new user token.
     *
     * @param personalAccessTokenRequestDto The token request
     */
    @Operation(summary = "Generate a new user personal access token")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(
                        responseCode = "400",
                        description = "Bad request",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
                @ApiResponse(
                        responseCode = "401",
                        description = "Authentication error, token expired or invalid",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
            })
    @PostMapping(value = "/v1/users/personal-access-token")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<PersonalAccessTokenResponseDto> createPersonalAccessToken(
            @Parameter(hidden = true) @AuthenticationPrincipal LocalUser connectedUser,
            @Parameter(name = "tokenRequestDto", description = "The token request", required = true) @RequestBody
                    PersonalAccessTokenRequestDto personalAccessTokenRequestDto) {
        String personalAccessToken = patHelperService.createPersonalAccessToken();
        Long checksum = patHelperService.computePersonAccessTokenChecksum(personalAccessToken);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(personalAccessTokenMapper.toPersonalAccessTokenDto(
                        patService.create(personalAccessTokenRequestDto.getName(), checksum, connectedUser),
                        personalAccessToken));
    }

    /** Delete a user personal access token. */
    @Operation(summary = "Delete a user personal access token")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "204", description = "Personal access token deleted"),
                @ApiResponse(
                        responseCode = "401",
                        description = "Authentication error, token expired or invalid",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
                @ApiResponse(
                        responseCode = "404",
                        description = "Personal access token not found",
                        content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
            })
    @DeleteMapping(value = "/v1/users/personal-access-token/{tokenName}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> deletePersonalAccessToken(
            @Parameter(hidden = true) @AuthenticationPrincipal LocalUser connectedUser,
            @Parameter(name = "tokenName", description = "The token name", required = true) @PathVariable("tokenName")
                    String tokenName) {
        Optional<PersonalAccessToken> tokenOptional = patService.findByNameAndUser(tokenName, connectedUser.getUser());

        if (tokenOptional.isEmpty()) {
            throw new ObjectNotFoundException(PersonalAccessToken.class, tokenName);
        }

        patService.deleteById(tokenOptional.get().getId());
        return ResponseEntity.noContent().build();
    }
}
