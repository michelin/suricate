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
import io.suricate.monitoring.service.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
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
        return users.stream().map(user -> new UserDto(user)).collect(Collectors.toList());
    }

    @RequestMapping(value="/search", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<UserDto> search(@RequestParam("username") String username) {
        Optional<List<User>> users = userService.getAllByUsernameStartWith(username);
        if(!users.isPresent()) {
            return new ArrayList<>();
        }

        return users.get().stream().map(user -> new UserDto(user)).collect(Collectors.toList());
    }

    /**
     * List a specific user
     * @return The user
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public UserDto getOne(@PathVariable("id") Long id) {
        Optional<User> user = userService.getOne(id);
        if (!user.isPresent()){
            throw new ApiException(ApiErrorEnum.USER_NOT_FOUND);
        }

        return new UserDto(user.get());
    }

    /**
     * Get current user
     * @return The user
     */
    @RequestMapping(value = "/current", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public UserDto getCurrentUser(Principal principal) {
        Optional<User> user = userService.getOneByUsername(principal.getName());

        if(!user.isPresent()) {
            throw new ApiException(ApiErrorEnum.USER_NOT_FOUND);
        }

        return new UserDto(user.get());
    }
}
