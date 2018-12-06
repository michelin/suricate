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

import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.websocket.WebsocketClient;
import io.suricate.monitoring.model.entity.project.Project;
import io.suricate.monitoring.service.api.ProjectService;
import io.suricate.monitoring.service.webSocket.DashboardWebSocketService;
import io.suricate.monitoring.utils.exception.ObjectNotFoundException;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Screen controller
 */
@RestController
@RequestMapping("/api")
@Api(value = "Screen controller", tags = {"Screen"})
public class ScreenController {

    /**
     * Class logger
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(ScreenController.class);

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
        @ApiResponse(code = 200, message = "Ok"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
        @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/screens/connect/{screenCode}/project/{projectToken}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public void connectProjectToScreen(@ApiParam(name = "projectToken", value = "The project token", required = true)
                                       @PathVariable("projectToken") String projectToken,
                                       @ApiParam(name = "screenCode", value = "The screen code", required = true)
                                       @PathVariable("screenCode") String screenCode) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);

        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectOptional);
        }

        this.dashboardWebSocketService.connectUniqueScreen(projectOptional.get(), screenCode);
    }

    /**
     * Disconnect a client
     *
     * @param websocketClient The web socket client to disconnect
     */
    @ApiOperation(value = "Send the notification to disconnect a new screen")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @PutMapping(value = "/v1/screens/disconnect")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public void disconnectProjectToTv(@ApiParam(name = "websocketClient", value = "websocket client to disconnect", required = true)
                                      @RequestBody WebsocketClient websocketClient) {
        this.dashboardWebSocketService.disconnectClient(websocketClient);
    }

    /**
     * Refresh every screen for a project token
     *
     * @param projectToken The project token used for the refresh
     */
    @ApiOperation(value = "Refresh every connected client for this project")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/screens/refresh/{projectToken}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void refreshEveryConnectedScreensForProject(@ApiParam(name = "projectToken", value = "The project token", required = true)
                                                       @PathVariable("projectToken") String projectToken) {
        this.dashboardWebSocketService.reloadAllConnectedDashboardForAProject(projectToken);
    }

    /**
     * Display the screen code on every connected dashboard
     *
     * @param projectToken The project token
     */
    @ApiOperation(value = "Send the notification to for the project screens to display their screen code")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok"),
        @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
        @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @GetMapping(value = "/v1/screens/screencode/{projectToken}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void displayScreenCodeEveryConnectedScreensForProject(@ApiParam(name = "projectToken", value = "The project token", required = true)
                                                                 @PathVariable("projectToken") String projectToken) {
        this.dashboardWebSocketService.displayScreenCodeForProject(projectToken);
    }
}
