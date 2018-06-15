package io.suricate.monitoring.controllers.api;

import io.suricate.monitoring.model.dto.user.RoleDto;
import io.suricate.monitoring.model.entity.user.Role;
import io.suricate.monitoring.model.mapper.role.RoleMapper;
import io.suricate.monitoring.service.api.RoleService;
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
            return ResponseEntity
                .noContent()
                .cacheControl(CacheControl.noCache())
                .build();
        }

        return ResponseEntity
            .ok()
            .cacheControl(CacheControl.noCache())
            .contentType(MediaType.APPLICATION_JSON)
            .body(roleMapper.toRoleDtosDefault(rolesOptional.get()));
    }
}
