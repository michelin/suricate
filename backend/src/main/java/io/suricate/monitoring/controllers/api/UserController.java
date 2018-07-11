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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
        @ApiResponse(code = 200, response = UserDto.class, message = "Ok", responseContainer = "List"),
        @ApiResponse(code = 401, response = ApiErrorDto.class, message = "Authentication error : Token expired or invalid")
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
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<UserDto>> search(@RequestParam("username") String username) {
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
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<UserDto> register(@RequestBody UserDto userDto) {
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
     * @param id The user id to get
     * @return The user
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserDto> getOne(@PathVariable("id") Long id) {
        Optional<User> user = userService.getOne(id);
        if (!user.isPresent()) {
            throw new ObjectNotFoundException(User.class, id);
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
    @Transactional
    public ResponseEntity<UserDto> deleteOne(@PathVariable("userId") Long userId) {
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
    @RequestMapping(value = "/{userId}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserDto> updateOne(@PathVariable("userId") Long userId, @RequestBody UserDto userDto) {
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
    @RequestMapping(value = "/{userId}/settings")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserDto> updateUserSettings(Principal principal,
                                                      @PathVariable("userId") Long userId,
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
    @RequestMapping(value = "/current", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal) {
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
