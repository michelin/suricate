/*
 *
 *  * Copyright 2012-2018 the original author or authors.
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

import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.services.api.ProjectService;
import io.suricate.monitoring.services.websocket.DashboardWebSocketService;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Screen controller
 */
@RestController
@RequestMapping("/api")
@Api(value = "Screen actions controller", tags = {"Screens"})
public class ScreenController {

    /**
     * The project service
     */
    private final ProjectService projectService;

    /**
     * The dashboard websocket service
     */
    private final DashboardWebSocketService dashboardWebSocketService;


    /**
     * Constructor
     *
     * @param projectService            The project service to inject
     * @param dashboardWebSocketService The dashboard websocket to inject
     */
    public ScreenController(final ProjectService projectService,
                            final DashboardWebSocketService dashboardWebSocketService) {
        this.projectService = projectService;
        this.dashboardWebSocketService = dashboardWebSocketService;
    }

    /**
     * connect a new Screen for a dashboard by screen code
     *
     * @param projectToken The project id we want to display
     * @param screenCode   The screen code to enroll
     */
    @ApiOperation(value = "Send the notification to connected a new screen")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Screen connected"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/screens/{projectToken}/connect")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public ResponseEntity<Void> connectProjectToScreen(@ApiParam(name = "projectToken", value = "The project token", required = true)
                                                       @PathVariable("projectToken") String projectToken,
                                                       @ApiParam(name = "screenCode", value = "The screen code", required = true)
                                                       @RequestParam("screenCode") String screenCode) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectOptional);
        }

        this.dashboardWebSocketService.sendConnectEventToScreenSubscriber(projectOptional.get(), screenCode);
        return ResponseEntity.noContent().build();
    }

    /**
     * Disconnect a client
     */
    @ApiOperation(value = "Send the notification to disconnect a new screen")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Screen disconnected"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/screens/{projectToken}/disconnect")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public ResponseEntity<Void> disconnectProjectToTv(@ApiParam(name = "projectToken", value = "The project token", required = true)
                                                      @PathVariable("projectToken") String projectToken,
                                                      @ApiParam(name = "screenCode", value = "The screen code", required = true)
                                                      @RequestParam("screenCode") String screenCode) {
        this.dashboardWebSocketService.disconnectClient(projectToken, Integer.parseInt(screenCode));
        return ResponseEntity.noContent().build();
    }

    /**
     * Refresh every screen for a project token
     *
     * @param projectToken The project token used for the refresh
     */
    @ApiOperation(value = "Refresh every connected client for this project")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Screens refresh"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/screens/{projectToken}/refresh")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> refreshEveryConnectedScreensForProject(@ApiParam(name = "projectToken", value = "The project token", required = true)
                                                                       @PathVariable("projectToken") String projectToken) {
        this.dashboardWebSocketService.reloadAllConnectedClientsToAProject(projectToken);
        return ResponseEntity.noContent().build();
    }

    /**
     * Display the screen code on every connected dashboard
     *
     * @param projectToken The project token
     */
    @ApiOperation(value = "Send the notification to for the project screens to display their screen code")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "Screen code displayed"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/screens/{projectToken}/showscreencode")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> displayScreenCodeEveryConnectedScreensForProject(@ApiParam(name = "projectToken", value = "The project token", required = true)
                                                                                 @PathVariable("projectToken") String projectToken) {
        this.dashboardWebSocketService.displayScreenCodeForProject(projectToken);
        return ResponseEntity.noContent().build();
    }
}
