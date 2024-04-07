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

package com.michelin.suricate.controller;

import com.michelin.suricate.model.dto.api.error.ApiErrorDto;
import com.michelin.suricate.model.dto.api.role.RoleResponseDto;
import com.michelin.suricate.model.dto.api.user.UserResponseDto;
import com.michelin.suricate.model.entity.Role;
import com.michelin.suricate.service.api.RoleService;
import com.michelin.suricate.service.mapper.RoleMapper;
import com.michelin.suricate.service.mapper.UserMapper;
import com.michelin.suricate.util.exception.ObjectNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Role controller.
 */
@RestController
@RequestMapping(value = "/api")
@Tag(name = "Role", description = "Role Controller")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * Get the list of roles.
     *
     * @return The list of roles
     */
    @Operation(summary = "Get the full list of roles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "204", description = "No Content"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = {
            @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource",
            content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
    })
    @PageableAsQueryParam
    @GetMapping(value = "/v1/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<RoleResponseDto> getRoles(@Parameter(name = "search", description = "Search keyword")
                                          @RequestParam(value = "search", required = false) String search,
                                          @Parameter(hidden = true) Pageable pageable) {
        return roleService.getRoles(search, pageable).map(roleMapper::toRoleDto);
    }

    /**
     * Get a role.
     *
     * @param roleId The role id to get
     * @return The role
     */
    @Operation(summary = "Get a role by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = {
            @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource",
            content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @GetMapping(value = "/v1/roles/{roleId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoleResponseDto> getOne(
        @Parameter(name = "roleId", description = "The role id", required = true, example = "1")
        @PathVariable("roleId") Long roleId) {
        Optional<Role> roleOptional = roleService.getOneById(roleId);
        if (roleOptional.isEmpty()) {
            throw new ObjectNotFoundException(Role.class, roleId);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(roleMapper.toRoleDto(roleOptional.get()));
    }

    /**
     * Get the list of users by role.
     *
     * @param roleId The role id to get
     * @return The list of related users
     */
    @Operation(summary = "Get the list of user for a role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = {
            @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource",
            content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @GetMapping(value = "/v1/roles/{roleId}/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getUsersByRole(
        @Parameter(name = "roleId", description = "The role id", required = true, example = "1")
        @PathVariable("roleId") Long roleId) {
        Optional<Role> roleOptional = roleService.getOneById(roleId);
        if (roleOptional.isEmpty()) {
            throw new ObjectNotFoundException(Role.class, roleId);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(userMapper.toUsersDtos(roleOptional.get().getUsers()));
    }
}
