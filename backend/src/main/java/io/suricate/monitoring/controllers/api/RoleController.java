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

import io.suricate.monitoring.model.dto.user.RoleDto;
import io.suricate.monitoring.model.entity.user.Role;
import io.suricate.monitoring.model.mapper.role.RoleMapper;
import io.suricate.monitoring.service.api.RoleService;
import io.suricate.monitoring.utils.exception.NoContentException;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Role managing controllers
 */
@RestController
@RequestMapping(value = "/api/roles")
@Api(value = "Role Controller", tags = {"Role"})
public class RoleController {

    /**
     * Class logger
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(RoleController.class);

    /**
     * The role service
     */
    private final RoleService roleService;

    /**
     * The role mapper
     */
    private final RoleMapper roleMapper;

    /**
     * Constructor
     *
     * @param roleService The role service
     * @param roleMapper  The role mapper
     */
    @Autowired
    public RoleController(final RoleService roleService,
                          final RoleMapper roleMapper) {
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    /**
     * Get the list of roles
     *
     * @return The list of roles
     */
    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<RoleDto>> getRoles() {
        Optional<List<Role>> rolesOptional = roleService.getRoles();

        if (!rolesOptional.isPresent()) {
            throw new NoContentException(Role.class);
        }

        return ResponseEntity
            .ok()
            .cacheControl(CacheControl.noCache())
            .contentType(MediaType.APPLICATION_JSON)
            .body(roleMapper.toRoleDtosDefault(rolesOptional.get()));
    }
}
