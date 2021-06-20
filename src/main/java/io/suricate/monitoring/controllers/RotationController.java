package io.suricate.monitoring.controllers;

import com.google.common.collect.Sets;
import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.api.rotation.RotationRequestDto;
import io.suricate.monitoring.model.dto.api.rotation.RotationResponseDto;
import io.suricate.monitoring.model.entities.Rotation;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.suricate.monitoring.services.api.RotationProjectService;
import io.suricate.monitoring.services.api.RotationService;
import io.suricate.monitoring.services.api.UserService;
import io.suricate.monitoring.services.mapper.RotationMapper;
import io.suricate.monitoring.utils.exceptions.ApiException;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 * Rotation controller
 */
@RestController
@RequestMapping("/api")
@Api(value = "Rotation Controller", tags = {"Rotations"})
public class RotationController {
    /**
     * Constant for users not allowed API exceptions
     */
    private static final String USER_NOT_ALLOWED = "The user is not allowed to access this rotation";

    /**
     * Rotation service
     */
    private final RotationService rotationService;

    /**
     * Rotation project service
     */
    private final RotationProjectService rotationProjectService;

    /**
     * User service
     */
    private final UserService userService;

    /**
     * The rotation mapper
     */
    private final RotationMapper rotationMapper;

    /**
     * Constructor
     *
     * @param rotationService The rotation service
     * @param rotationProjectService The rotation project service
     * @param rotationMapper The rotation mapper
     * @param userService The user service
     */
    @Autowired
    public RotationController(final RotationService rotationService,
                              final RotationProjectService rotationProjectService,
                              final RotationMapper rotationMapper,
                              final UserService userService) {
        this.rotationService = rotationService;
        this.rotationProjectService = rotationProjectService;
        this.rotationMapper = rotationMapper;
        this.userService = userService;
    }

    /**
     * Get a rotation by id
     *
     * @param authentication The connected user
     * @param id The id of the rotation
     * @return The rotation
     */
    @ApiOperation(value = "Retrieve the rotation information by id", response = RotationResponseDto.class, nickname = "getRotationById")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = RotationResponseDto.class),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
            @ApiResponse(code = 404, message = "Rotation not found", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/rotations/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<RotationResponseDto> getRotationById(@ApiIgnore OAuth2Authentication authentication,
                                                               @ApiParam(name = "id", value = "The rotation id", required = true)
                                                               @PathVariable("id") Long id) {
        Optional<Rotation> rotationOptional = this.rotationService.getOneById(id);

        if (!rotationOptional.isPresent()) {
            throw new ObjectNotFoundException(Rotation.class, id);
        }

        if (!this.rotationService.isConnectedUserCanAccessToRotation(rotationOptional.get(), authentication.getUserAuthentication())) {
            throw new ApiException(RotationController.USER_NOT_ALLOWED, ApiErrorEnum.NOT_AUTHORIZED);
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.rotationMapper.toRotationDTO(rotationOptional.get()));
    }

    /**
     * Create a new rotation
     *
     * @param rotationRequestDto The rotation to create
     * @return The created rotation
     */
    @ApiOperation(value = "Create a new rotation", response = RotationResponseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = RotationResponseDto.class),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
            @ApiResponse(code = 404, message = "Current user not found", response = ApiErrorDto.class)
    })
    @PostMapping(value = "/v1/rotations")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<RotationResponseDto> create(@ApiParam(name = "rotationRequestDto", value = "The rotation information", required = true)
                       @RequestBody RotationRequestDto rotationRequestDto) {
        Rotation rotationCreated = this.rotationService.create(this.rotationMapper.toRotationEntity(rotationRequestDto));

        rotationCreated.getRotationProjects().forEach(rotationProject -> rotationProject.setRotation(rotationCreated));

        rotationCreated.setRotationProjects(
            Sets.newLinkedHashSet(this.rotationProjectService.createAll(rotationCreated.getRotationProjects())));

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.rotationMapper.toRotationDTO(rotationCreated));
    }

    /**
     * Get rotations for a user
     *
     * @param principal The connected user
     * @return The whole list of rotations
     */
    @ApiOperation(value = "Get the list of rotations related to the current user", response = RotationResponseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = RotationResponseDto.class, responseContainer = "List"),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
            @ApiResponse(code = 404, message = "Current user not found", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/rotations/currentUser")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public ResponseEntity<List<RotationResponseDto>> getAllForCurrentUser(@ApiIgnore Principal principal) {
        Optional<User> userOptional = userService.getOneByUsername(principal.getName());

        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException(User.class, principal.getName());
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.rotationMapper.toRotationsDTOs(this.rotationService.getAllByUser(userOptional.get())));
    }
}
