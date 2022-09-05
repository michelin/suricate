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
import io.suricate.monitoring.model.dto.api.role.RoleResponseDto;
import io.suricate.monitoring.model.dto.api.user.UserResponseDto;
import io.suricate.monitoring.model.entities.Role;
import io.suricate.monitoring.services.api.RoleService;
import io.suricate.monitoring.services.mapper.RoleMapper;
import io.suricate.monitoring.services.mapper.UserMapper;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Role managing controllers
 */
@RestController
@RequestMapping(value = "/api")
@Api(value = "Role Controller", tags = {"Roles"})
public class RoleController {

    /**
     * The role service
     */
    private final RoleService roleService;

    /**
     * The role mapper
     */
    private final RoleMapper roleMapper;

    /**
     * The user mapper
     */
    private final UserMapper userMapper;

    /**
     * Constructor
     *
     * @param roleService The role service
     * @param roleMapper  The role mapper
     * @param userMapper  The user mapper
     */
    @Autowired
    public RoleController(final RoleService roleService,
                          final RoleMapper roleMapper,
                          final UserMapper userMapper) {
        this.roleService = roleService;
        this.roleMapper = roleMapper;
        this.userMapper = userMapper;
    }

    /**
     * Get the list of roles
     *
     * @return The list of roles
     */
    @ApiOperation(value = "Get the full list of roles", response = RoleResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = RoleResponseDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
    })
    @ApiPageable
    @GetMapping(value = "/v1/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<RoleResponseDto> getRoles(@ApiParam(name = "search", value = "Search keyword")
                                          @RequestParam(value = "search", required = false) String search,
                                          Pageable pageable) {
        Page<Role> rolesPaged = roleService.getRoles(search, pageable);
        return rolesPaged.map(roleMapper::toRoleDTO);
    }

    /**
     * Get a role
     *
     * @param roleId The role id to get
     * @return The role
     */
    @ApiOperation(value = "Get a role by id", response = RoleResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = RoleResponseDto.class),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/roles/{roleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoleResponseDto> getOne(@ApiParam(name = "roleId", value = "The role id", required = true, example = "1")
                                                  @PathVariable("roleId") Long roleId) {
        Optional<Role> roleOptional = roleService.getOneById(roleId);
        if (!roleOptional.isPresent()) {
            throw new ObjectNotFoundException(Role.class, roleId);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(roleMapper.toRoleDTO(roleOptional.get()));
    }

    /**
     * Get the list of users by role
     *
     * @param roleId The role id to get
     * @return The list of related users
     */
    @ApiOperation(value = "Get the list of user for a role", response = UserResponseDto.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = UserResponseDto.class, responseContainer = "List"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/roles/{roleId}/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getUsersByRole(@ApiParam(name = "roleId", value = "The role id", required = true, example = "1")
                                                                @PathVariable("roleId") Long roleId) {
        Optional<Role> roleOptional = roleService.getOneById(roleId);
        if (!roleOptional.isPresent()) {
            throw new ObjectNotFoundException(Role.class, roleId);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(userMapper.toUsersDTOs(roleOptional.get().getUsers()));
    }
}
