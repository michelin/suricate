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
import io.suricate.monitoring.model.dto.api.token.PersonalAccessTokenResponseDto;
import io.suricate.monitoring.model.dto.api.token.PersonalAccessTokenRequestDto;
import io.suricate.monitoring.model.dto.api.user.UserRequestDto;
import io.suricate.monitoring.model.dto.api.user.UserResponseDto;
import io.suricate.monitoring.model.dto.api.user.UserSettingRequestDto;
import io.suricate.monitoring.model.dto.api.user.UserSettingResponseDto;
import io.suricate.monitoring.model.entities.Setting;
import io.suricate.monitoring.model.entities.PersonalAccessToken;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.model.entities.UserSetting;
import io.suricate.monitoring.model.enums.AuthenticationProvider;
import io.suricate.monitoring.security.LocalUser;
import io.suricate.monitoring.services.api.SettingService;
import io.suricate.monitoring.services.api.PersonalAccessTokenService;
import io.suricate.monitoring.services.api.UserService;
import io.suricate.monitoring.services.api.UserSettingService;
import io.suricate.monitoring.services.mapper.PersonalAccessTokenMapper;
import io.suricate.monitoring.services.mapper.UserMapper;
import io.suricate.monitoring.services.mapper.UserSettingMapper;
import io.suricate.monitoring.services.token.PersonalAccessTokenHelperService;
import io.suricate.monitoring.utils.exceptions.EmailAlreadyExistException;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import io.suricate.monitoring.utils.exceptions.UsernameAlreadyExistException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

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
    @Autowired
    private UserService userService;

    /**
     * The user setting service
     */
    @Autowired
    private UserSettingService userSettingService;

    /**
     * The setting service
     */
    @Autowired
    private SettingService settingService;

    /**
     * The personal access token helper service
     */
    @Autowired
    private PersonalAccessTokenHelperService patHelperService;

    /**
     * The personal access token service
     */
    @Autowired
    private PersonalAccessTokenService patService;

    /**
     * The user mapper
     */
    @Autowired
    private UserMapper userMapper;

    /**
     * The user setting mapper
     */
    @Autowired
    private UserSettingMapper userSettingMapper;

    /**
     * The token mapper
     */
    @Autowired
    private PersonalAccessTokenMapper personalAccessTokenMapper;

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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<UserResponseDto> getAll(@ApiParam(name = "search", value = "Search keyword")
                                        @RequestParam(value = "search", required = false) String search,
                                        Pageable pageable) {
        return userService.getAll(search, pageable)
                .map(userMapper::toUserDTO);
    }

    /**
     * Get a specific user
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserResponseDto> getOne(@ApiParam(name = "userId", value = "The user id", required = true, example = "1")
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
    public ResponseEntity<Void> updateOne(@ApiParam(name = "userId", value = "The user id", required = true, example = "1")
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
    public ResponseEntity<Void> deleteOne(@ApiParam(name = "userId", value = "The user id", required = true, example = "1")
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
     * @param username The username
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
     * @param userName              The username used in the url
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
                                                              @ApiParam(name = "settingId", value = "The setting id", required = true, example = "1")
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
     * @param userRequestDto The user to register
     * @return The user registered
     */
    @ApiOperation(value = "Register a new user", response = UserResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = UserResponseDto.class),
        @ApiResponse(code = 400, message = "Bad request", response = ApiErrorDto.class),
        @ApiResponse(code = 409, message = "Username or email already taken", response = ApiErrorDto.class)
    })
    @PostMapping(value = "/v1/users/signup")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<UserResponseDto> signUp(@ApiParam(name = "userResponseDto", value = "The user information to create", required = true)
                                                  @RequestBody UserRequestDto userRequestDto) {
        if (userService.getOneByUsername(userRequestDto.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistException(userRequestDto.getUsername());
        }

        if (userService.getOneByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistException(userRequestDto.getEmail());
        }

        User user = userMapper.toUserEntity(userRequestDto, AuthenticationProvider.DATABASE);
        User savedUser = userService.create(user);

        URI resourceLocation = ServletUriComponentsBuilder
            .fromCurrentContextPath()
            .path("/api/users/" + savedUser.getId())
            .build()
            .toUri();

        return ResponseEntity
            .created(resourceLocation)
            .contentType(MediaType.APPLICATION_JSON)
            .body(userMapper.toUserDTO(savedUser));
    }

    /**
     * Get all user personal access tokens
     * @param connectedUser The authentication principal as LocalUser
     * @return A list of personal access tokens
     */
    @ApiOperation(value = "Get all user personal access tokens", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = List.class),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/users/personal-access-token")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<PersonalAccessTokenResponseDto>> getPersonalAccessTokens(@ApiIgnore @AuthenticationPrincipal LocalUser connectedUser) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(personalAccessTokenMapper.toPersonalAccessTokensDTOs(patService.findAllByUser(connectedUser.getUser())));
    }

    /**
     * Generate a new user token
     * @param personalAccessTokenRequestDto The token request
     */
    @ApiOperation(value = "Generate a new user personal access token", response = PersonalAccessTokenResponseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = PersonalAccessTokenResponseDto.class),
            @ApiResponse(code = 400, message = "Bad request", response = ApiErrorDto.class),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class)
    })
    @PostMapping(value = "/v1/users/personal-access-token")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<PersonalAccessTokenResponseDto> createPersonalAccessToken(@ApiIgnore Authentication authentication,
                                                                                    @ApiParam(name = "tokenRequestDto", value = "The token request", required = true)
                                                        @RequestBody PersonalAccessTokenRequestDto personalAccessTokenRequestDto) {
        String personalAccessToken = patHelperService.createPersonalAccessToken();
        Long checksum = patHelperService.computePersonAccessTokenChecksum(personalAccessToken);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(personalAccessTokenMapper.toPersonalAccessTokenDTO(patService.create(personalAccessTokenRequestDto.getName(), checksum, authentication), personalAccessToken));
    }

    /**
     * Delete a user personal access token
     */
    @ApiOperation(value = "Delete a user personal access token", response = PersonalAccessTokenResponseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Personal access token deleted"),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 404, message = "Personal access token not found", response = ApiErrorDto.class)
    })
    @DeleteMapping(value = "/v1/users/personal-access-token/{tokenName}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> deletePersonalAccessToken(@ApiIgnore @AuthenticationPrincipal LocalUser connectedUser,
                                            @ApiParam(name = "tokenName", value = "The token name", required = true)
                                            @PathVariable("tokenName") String tokenName) {
        Optional<PersonalAccessToken> tokenOptional = patService.findByNameAndUser(tokenName, connectedUser.getUser());
        if (!tokenOptional.isPresent()) {
            throw new ObjectNotFoundException(PersonalAccessToken.class, tokenName);
        }

        patService.deleteById(tokenOptional.get().getId());
        return ResponseEntity.noContent().build();
    }
}
