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

import io.suricate.monitoring.model.dto.websocket.WebsocketClient;
import io.suricate.monitoring.model.entity.project.Project;
import io.suricate.monitoring.service.api.ProjectService;
import io.suricate.monitoring.service.webSocket.DashboardWebSocketService;
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
@RequestMapping("/api/screens")
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
     * @param projectService The project service to inject
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
     * @param screenCode The screen code to enroll
     */
    @RequestMapping(value = "connect/{screenCode}/project/{projectToken}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public void connectProjectToScreen(@PathVariable("projectToken") String projectToken,
                                       @PathVariable("screenCode") String screenCode) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
        projectOptional.ifPresent(project -> this.dashboardWebSocketService.connectUniqueScreen(project, screenCode));
    }

    /**
     * Disconnect a client
     *
     * @param websocketClient The web socket client to disconnect
     */
    @RequestMapping(value = "disconnect", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    public void disconnectProjectToTv(@RequestBody WebsocketClient websocketClient) {
        this.dashboardWebSocketService.disconnectClient(websocketClient);
    }

    /**
     * Refresh every screen for a project token
     *
     * @param projectToken The project token used for the refresh
     */
    @RequestMapping(value = "refresh/{projectToken}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public void refreshEveryConnectedScreensForProject(@PathVariable("projectToken") String projectToken) {
        this.dashboardWebSocketService.reloadAllConnectedDashboardForAProject(projectToken);
    }
}
