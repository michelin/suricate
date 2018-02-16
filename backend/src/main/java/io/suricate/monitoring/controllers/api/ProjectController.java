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

import io.suricate.monitoring.model.entity.project.ProjectWidget;
import io.suricate.monitoring.model.dto.project.ProjectResponse;
import io.suricate.monitoring.model.dto.project.ProjectWidgetRequest;
import io.suricate.monitoring.model.entity.user.User;
import io.suricate.monitoring.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<ProjectResponse> getAll(@RequestAttribute User user) {
        return projectService.getAllByUser(user);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ProjectResponse getOneById(@RequestAttribute User user, @PathVariable("id") Long id) {
        return projectService.getOneById(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ProjectResponse addWidgetToProject(@RequestAttribute User user,
                                              @PathVariable("id") Long id,
                                              @RequestBody ProjectWidgetRequest projectWidgetRequest) {
        ProjectWidget projectWidget = projectService.addWidgetToProject(projectWidgetRequest);
        return projectService.getOneById(projectWidget.getProject().getId());
    }
}
