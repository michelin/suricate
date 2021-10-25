package io.suricate.monitoring.controllers;

import io.suricate.monitoring.configuration.swagger.ApiPageable;
import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.api.project.ProjectResponseDto;
import io.suricate.monitoring.model.dto.api.projectwidget.ProjectWidgetRequestDto;
import io.suricate.monitoring.model.dto.api.projectwidget.ProjectWidgetResponseDto;
import io.suricate.monitoring.model.dto.api.rotation.RotationRequestDto;
import io.suricate.monitoring.model.dto.api.rotation.RotationResponseDto;
import io.suricate.monitoring.model.dto.api.rotationproject.RotationProjectRequestDto;
import io.suricate.monitoring.model.dto.api.rotationproject.RotationProjectResponseDto;
import io.suricate.monitoring.model.dto.api.user.UserResponseDto;
import io.suricate.monitoring.model.dto.websocket.WebsocketClient;
import io.suricate.monitoring.model.entities.*;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.suricate.monitoring.services.api.RotationProjectService;
import io.suricate.monitoring.services.api.RotationService;
import io.suricate.monitoring.services.api.UserService;
import io.suricate.monitoring.services.mapper.RotationMapper;
import io.suricate.monitoring.services.mapper.RotationProjectMapper;
import io.suricate.monitoring.services.mapper.UserMapper;
import io.suricate.monitoring.services.websocket.DashboardWebSocketService;
import io.suricate.monitoring.services.websocket.RotationWebSocketService;
import io.suricate.monitoring.utils.exceptions.ApiException;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.security.PermitAll;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
     * The rotation websocket service
     */
    private final RotationWebSocketService rotationWebSocketService;

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
     * The user mapper
     */
    private final UserMapper userMapper;

    /**
     * The rotation project mapper
     */
    private final RotationProjectMapper rotationProjectMapper;

    /**
     * Constructor
     *
     * @param rotationService The rotation service
     * @param rotationProjectService The rotation project service
     * @param rotationMapper The rotation mapper
     * @param rotationProjectMapper The rotation project mapper
     * @param userMapper The user mapper
     * @param userService The user service
     * @param rotationWebSocketService The rotation web socket service
     */
    public RotationController(final RotationService rotationService,
                              final RotationProjectService rotationProjectService,
                              final RotationMapper rotationMapper,
                              final RotationProjectMapper rotationProjectMapper,
                              final UserMapper userMapper,
                              final RotationWebSocketService rotationWebSocketService,
                              final UserService userService) {
        this.rotationService = rotationService;
        this.rotationProjectService = rotationProjectService;
        this.rotationMapper = rotationMapper;
        this.rotationProjectMapper = rotationProjectMapper;
        this.userMapper = userMapper;
        this.userService = userService;
        this.rotationWebSocketService = rotationWebSocketService;
    }

    /**
     * Get every rotation in database
     *
     * @return The whole list of rotations
     */
    @ApiOperation(value = "Get the full list of rotations", response = RotationResponseDto.class, nickname = "getAllRotations")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = RotationResponseDto.class, responseContainer = "List"),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @ApiPageable
    @GetMapping("/v1/rotations")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Page<RotationResponseDto> getAll(@ApiParam(name = "search", value = "Search keyword")
                                           @RequestParam(value = "search", required = false) String search,
                                           Pageable pageable) {
        return this.rotationService.getAll(search, pageable)
                .map(this.rotationMapper::toRotationDTO);
    }

    /**
     * Get a rotation by token
     *
     * @param authentication The connected user
     * @param rotationToken  The rotation token
     * @return The rotation
     */
    @ApiOperation(value = "Retrieve the rotation information by id", response = RotationResponseDto.class, nickname = "getRotationById")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = RotationResponseDto.class),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
            @ApiResponse(code = 404, message = "Rotation not found", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/rotations/{rotationToken}")
    @PermitAll
    public ResponseEntity<RotationResponseDto> getRotationByToken(@ApiParam(name = "rotationToken", value = "The rotation token", required = true)
                                                                  @PathVariable("rotationToken") String rotationToken) {
        Optional<Rotation> rotationOptional = this.rotationService.getOneByToken(rotationToken);

        if (!rotationOptional.isPresent()) {
            throw new ObjectNotFoundException(Rotation.class, rotationToken);
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
    public ResponseEntity<RotationResponseDto> create(@ApiIgnore Principal principal,
                                                      @ApiParam(name = "rotationRequestDto", value = "The rotation information", required = true)
                                                      @RequestBody RotationRequestDto rotationRequestDto) {
        Optional<User> userOptional = this.userService.getOneByUsername(principal.getName());
        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException(User.class, principal.getName());
        }

        Rotation rotationCreated = this.rotationService.create(userOptional.get(),
                this.rotationMapper.toRotationEntity(rotationRequestDto));

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

    /**
     * Delete a rotation
     *
     * @param authentication The connected user
     * @param rotationToken  The rotation token
     * @return A void response entity
     */
    @ApiOperation(value = "Delete a rotation by the token")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Rotation deleted"),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
            @ApiResponse(code = 404, message = "Rotation not found", response = ApiErrorDto.class)
    })
    @DeleteMapping(value = "/v1/rotations/{rotationToken}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> deleteOneByToken(@ApiIgnore OAuth2Authentication authentication,
                                                 @ApiParam(name = "rotationToken", value = "The rotation token", required = true)
                                                 @PathVariable("rotationToken") String rotationToken) {
        Optional<Rotation> rotationOptional = this.rotationService.getOneByToken(rotationToken);

        if (!rotationOptional.isPresent()) {
            throw new ObjectNotFoundException(Rotation.class, rotationToken);
        }

        if (!this.rotationService.isConnectedUserCanAccessToRotation(rotationOptional.get(), authentication.getUserAuthentication())) {
            throw new ApiException(RotationController.USER_NOT_ALLOWED, ApiErrorEnum.NOT_AUTHORIZED);
        }

        this.rotationService.deleteRotation(rotationOptional.get());

        return ResponseEntity.noContent().build();
    }

    /**
     * Update an existing rotation
     *
     * @param authentication     The connected user
     * @param rotationToken      The rotation token
     * @param rotationRequestDto The information to update
     * @return A void response entity
     */
    @ApiOperation(value = "Update an existing rotation by the id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Rotation updated"),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
            @ApiResponse(code = 404, message = "Rotation not found", response = ApiErrorDto.class)
    })
    @PutMapping(value = "/v1/rotations/{rotationToken}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> updateRotation(@ApiIgnore OAuth2Authentication authentication,
                                               @ApiParam(name = "rotationToken", value = "The rotation token", required = true)
                                               @PathVariable("rotationToken") String rotationToken,
                                               @ApiParam(name = "rotationRequestDto", value = "The rotation information", required = true)
                                               @RequestBody RotationRequestDto rotationRequestDto) {
        Optional<Rotation> rotationOptional = this.rotationService.getOneByToken(rotationToken);

        if (!rotationOptional.isPresent()) {
            throw new ObjectNotFoundException(Rotation.class, rotationToken);
        }

        if (!this.rotationService.isConnectedUserCanAccessToRotation(rotationOptional.get(), authentication.getUserAuthentication())) {
            throw new ApiException(RotationController.USER_NOT_ALLOWED, ApiErrorEnum.NOT_AUTHORIZED);
        }

        this.rotationService.updateRotation(rotationOptional.get(), rotationRequestDto);

        return ResponseEntity.noContent().build();
    }

    /**
     * Get the list of rotation projects for a rotation
     */
    @ApiOperation(value = "Get the full list of rotation projects for a rotation", response = RotationProjectResponseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = RotationProjectResponseDto.class, responseContainer = "List"),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/rotations/{rotationToken}/rotationProjects")
    @PermitAll
    public ResponseEntity<List<RotationProjectResponseDto>> getRotationProjectsForRotation(@ApiParam(name = "rotationToken", value = "The rotation token", required = true)
                                                                                           @PathVariable("rotationToken") String rotationToken) {
        Optional<Rotation> rotationOptional = this.rotationService.getOneByToken(rotationToken);

        if (!rotationOptional.isPresent()) {
            throw new ObjectNotFoundException(Rotation.class, rotationToken);
        }

        Rotation rotation = rotationOptional.get();
        if (rotation.getRotationProjects().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.rotationProjectMapper.toRotationProjectDTOs(rotation.getRotationProjects()));
    }

    /**
     * Add projects into the rotation
     *
     * @param authentication             The connected user
     * @param rotationToken              The rotation token
     * @param rotationProjectRequestDtos The list of projects to add
     * @return The list of added projects
     */
    @ApiOperation(value = "Add new projects to a rotation", response = ProjectWidgetResponseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = RotationProjectRequestDto.class),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
            @ApiResponse(code = 404, message = "Rotation not found", response = ApiErrorDto.class)
    })
    @PostMapping(value = "/v1/rotations/{rotationToken}/projects")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<RotationProjectResponseDto>> addProjectsToRotation(@ApiIgnore OAuth2Authentication authentication,
                                                                                  @ApiParam(name = "rotationToken", value = "The rotation token", required = true)
                                                                                  @PathVariable("rotationToken") String rotationToken,
                                                                                  @ApiParam(name = "rotationProjectRequestDtos", value = "The list of rotation projects", required = true)
                                                                                  @RequestBody List<RotationProjectRequestDto> rotationProjectRequestDtos) {
        Optional<Rotation> rotationOptional = this.rotationService.getOneByToken(rotationToken);

        if (!rotationOptional.isPresent()) {
            throw new ObjectNotFoundException(Rotation.class, rotationToken);
        }

        if (!this.rotationService.isConnectedUserCanAccessToRotation(rotationOptional.get(), authentication.getUserAuthentication())) {
            throw new ApiException(RotationController.USER_NOT_ALLOWED, ApiErrorEnum.NOT_AUTHORIZED);
        }

        List<RotationProject> rotationProjects = rotationProjectRequestDtos
                .stream()
                .map(rotationProjectRequestDto -> this.rotationProjectMapper
                        .toRotationProjectEntity(rotationProjectRequestDto, rotationToken))
                .collect(Collectors.toList());

        this.rotationProjectService
                .addProjectsToRotation(rotationOptional.get(), rotationProjects);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.rotationProjectMapper.toRotationProjectDTOs(rotationProjects));
    }

    /**
     * Get the list of websocket clients connected to the rotation
     *
     * @param rotationToken The rotation token
     */
    @ApiOperation(value = "Retrieve connected websocket clients for a rotation", response = WebsocketClient.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = WebsocketClient.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/rotations/{rotationToken}/websocket/clients")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<WebsocketClient>> getRotationWebsocketClients(@ApiParam(name = "rotationToken", value = "The rotation token", required = true)
                                                                             @PathVariable("rotationToken") String rotationToken) {
        Optional<Rotation> rotationOptional = this.rotationService.getOneByToken(rotationToken);
        if (!rotationOptional.isPresent()) {
            throw new ObjectNotFoundException(Rotation.class, rotationToken);
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.rotationWebSocketService.getWebsocketClientsByRotationToken(rotationToken));
    }

    /**
     * Get the list of users associated to a rotation
     *
     * @param rotationToken The rotation token
     */
    @ApiOperation(value = "Retrieve rotation users", response = UserResponseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = UserResponseDto.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/rotations/{rotationToken}/users")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<UserResponseDto>> getRotationUsers(@ApiParam(name = "rotationToken", value = "The rotation token", required = true)
                                                                  @PathVariable("rotationToken") String rotationToken) {
        Optional<Rotation> rotationOptional = this.rotationService.getOneByToken(rotationToken);
        if (!rotationOptional.isPresent()) {
            throw new ObjectNotFoundException(Rotation.class, rotationToken);
        }

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.userMapper.toUsersDTOs(rotationOptional.get().getUsers()));
    }

    /**
     * Add a user to a rotation
     *
     * @param authentication The connected user
     * @param rotationToken  Token of the rotation
     * @param usernameMap    Username of the user to add
     * @return An empty HTTP response
     */
    @ApiOperation(value = "Add a user to a rotation")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
            @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class),
            @ApiResponse(code = 404, message = "User not found", response = ApiErrorDto.class)
    })
    @PostMapping(value = "/v1/rotations/{rotationToken}/users")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> addUserToRotation(@ApiIgnore OAuth2Authentication authentication,
                                                 @ApiParam(name = "rotationToken", value = "The rotation token", required = true)
                                                 @PathVariable("rotationToken") String rotationToken,
                                                 @ApiParam(name = "usernameMap", value = "A map with the username", required = true)
                                                 @RequestBody Map<String, String> usernameMap) {

        Optional<Rotation> rotationOptional = this.rotationService.getOneByToken(rotationToken);

        if (!rotationOptional.isPresent()) {
            throw new ObjectNotFoundException(Rotation.class, rotationToken);
        }

        if (!this.rotationService.isConnectedUserCanAccessToRotation(rotationOptional.get(), authentication.getUserAuthentication())) {
            throw new ApiException(RotationController.USER_NOT_ALLOWED, ApiErrorEnum.NOT_AUTHORIZED);
        }

        Optional<User> userOptional = this.userService.getOneByUsername(usernameMap.get("username"));
        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException(User.class, usernameMap.get("username"));
        }

        this.rotationService.addUserToRotation(userOptional.get(), rotationOptional.get());
        return ResponseEntity.ok().build();
    }

    /**
     * Delete a user from a rotation
     *
     * @param authentication The connected user
     * @param rotationToken  The rotation token
     * @param userId         The user id to delete
     * @return Empty HTTP response
     */
    @ApiOperation(value = "Delete a user from a rotation")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "User removed from rotation"),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
            @ApiResponse(code = 404, message = "Rotation not found", response = ApiErrorDto.class),
            @ApiResponse(code = 404, message = "User not found", response = ApiErrorDto.class)
    })
    @DeleteMapping(value = "/v1/rotations/{rotationToken}/users/{userId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> deleteUserFromRotation(@ApiIgnore OAuth2Authentication authentication,
                                                    @ApiParam(name = "rotationToken", value = "The rotation token", required = true)
                                                    @PathVariable("rotationToken") String rotationToken,
                                                    @ApiParam(name = "userId", value = "The user id", required = true)
                                                    @PathVariable("userId") Long userId) {
        Optional<Rotation> rotationOptional = this.rotationService.getOneByToken(rotationToken);
        if (!rotationOptional.isPresent()) {
            throw new ObjectNotFoundException(Rotation.class, rotationOptional);
        }

        if (!this.rotationService.isConnectedUserCanAccessToRotation(rotationOptional.get(), authentication.getUserAuthentication())) {
            throw new ApiException(RotationController.USER_NOT_ALLOWED, ApiErrorEnum.NOT_AUTHORIZED);
        }

        Optional<User> userOptional = this.userService.getOne(userId);
        if (!userOptional.isPresent()) {
            throw new ObjectNotFoundException(User.class, userId);
        }

        this.rotationService.deleteUserFromRotation(userOptional.get(), rotationOptional.get());
        return ResponseEntity.noContent().build();
    }
}
