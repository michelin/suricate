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

package com.michelin.suricate.controllers;

import com.michelin.suricate.model.dto.api.error.ApiErrorDto;
import com.michelin.suricate.model.entities.Project;
import com.michelin.suricate.model.dto.websocket.UpdateEvent;
import com.michelin.suricate.model.enums.UpdateType;
import com.michelin.suricate.services.api.ProjectService;
import com.michelin.suricate.services.websocket.DashboardWebSocketService;
import com.michelin.suricate.utils.exceptions.ObjectNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@Tag(name = "Screen", description = "Screen Controller")
public class ScreenController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private DashboardWebSocketService dashboardWebSocketService;

    /**
     * Connect a new screen for a dashboard by screen code
     * @param projectToken The project id we want to display
     * @param screenCode   The screen code to enroll
     */
    @Operation(summary = "Send the notification to connected a new screen")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Screen connected"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "404", description = "Project not found", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @GetMapping(value = "/v1/screens/{projectToken}/connect")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> connectProjectToScreen(@Parameter(name = "projectToken", description = "The project token", required = true)
                                                       @PathVariable("projectToken") String projectToken,
                                                       @Parameter(name = "screenCode", description = "The screen code", required = true)
                                                       @RequestParam("screenCode") String screenCode) {
        Optional<Project> projectOptional = this.projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectOptional);
        }

        this.dashboardWebSocketService.sendConnectProjectEventToScreenSubscriber(projectOptional.get(), screenCode);
        return ResponseEntity.noContent().build();
    }

    /**
     * Disconnect a client from a project
     */
    @Operation(summary = "Send the notification to disconnect a new screen")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Screen disconnected"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @GetMapping(value = "/v1/screens/{projectToken}/disconnect")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> disconnectProjectFromScreen(@Parameter(name = "projectToken", description = "The project token", required = true)
                                                      @PathVariable("projectToken") String projectToken,
                                                      @Parameter(name = "screenCode", description = "The screen code", required = true)
                                                      @RequestParam("screenCode") String screenCode) {
        this.dashboardWebSocketService.disconnectClient(projectToken, screenCode);
        return ResponseEntity.noContent().build();
    }

    /**
     * Refresh every screen for a project token
     * @param projectToken The project token used for the refresh
     */
    @Operation(summary = "Refresh every connected client for this project")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Screens refresh"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @GetMapping(value = "/v1/screens/{projectToken}/refresh")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> refreshEveryConnectedScreensForProject(@Parameter(name = "projectToken", description = "The project token", required = true)
                                                                       @PathVariable("projectToken") String projectToken) {
        this.dashboardWebSocketService.reloadAllConnectedClientsToAProject(projectToken);
        return ResponseEntity.noContent().build();
    }

    /**
     * Display the screen code on every connected dashboard
     * @param projectToken The project token
     */
    @Operation(summary = "Send the notification to the project screens to display their screen code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Screen code displayed"),
        @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @GetMapping(value = "/v1/screens/{projectToken}/showscreencode")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> displayScreenCodeEveryConnectedScreensForProject(@Parameter(name = "projectToken", description = "The project token", required = true)
                                                                                 @PathVariable("projectToken") String projectToken) {
        Optional<Project> projectOptional = this.projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectOptional);
        }

        this.dashboardWebSocketService
                .sendEventToProjectSubscribers(projectToken, UpdateEvent.builder()
                        .type(UpdateType.DISPLAY_NUMBER)
                        .build());

        return ResponseEntity
                .noContent()
                .build();
    }

    /**
     * Count the number of connected dashboards through websockets
     */
    @Operation(summary = "Count the number of connected dashboards through websockets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "Authentication error, token expired or invalid", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))}),
            @ApiResponse(responseCode = "403", description = "You don't have permission to access to this resource", content = { @Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @GetMapping(value = "/v1/screens/count")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Integer> getConnectedScreensQuantity() {
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(dashboardWebSocketService.countWebsocketClients());
    }
}
