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

import io.suricate.monitoring.controllers.api.error.exception.ApiException;
import io.suricate.monitoring.model.dto.project.ProjectDto;
import io.suricate.monitoring.model.entity.project.Project;
import io.suricate.monitoring.model.entity.project.ProjectWidget;
import io.suricate.monitoring.model.dto.project.ProjectWidgetDto;
import io.suricate.monitoring.model.entity.user.User;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.suricate.monitoring.service.api.ProjectService;
import io.suricate.monitoring.service.api.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Project controller
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    /**
     * Class logger
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(Project.class);

    /**
     * Project service
     */
    private final ProjectService projectService;
    /**
     * User service
     */
    private final UserService userService;

    /**
     * Constructor for dependency injection
     *
     * @param projectService The project service to inject
     * @param userService The user service to inject
     */
    @Autowired
    public ProjectController(final ProjectService projectService, final UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    /**
     * Get every project in database
     *
     * @param principal The connected user
     * @return The whole list of projects
     */
    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<ProjectDto> getAll(Principal principal) {
        //TODO: Dispatch the logic in two separated methods
        List<Project> projects;
        Collection<GrantedAuthority> authorities = ((OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication()).getAuthorities();

        if(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            projects = projectService.getAll();

        } else {
            Optional<User> user = userService.getOneByUsername(principal.getName());
            if(!user.isPresent()) {
                throw new ApiException(ApiErrorEnum.USER_NOT_FOUND);
            }

            projects = projectService.getAllByUser(user.get());
        }

        return projects.stream().map(project -> projectService.toDTO(project, true)).collect(Collectors.toList());
    }

    /**
     * Add a new project/dashboard for a user
     *
     * @param principal The connected user
     * @param projectDto The project to add
     * @return The saved project
     */
    @RequestMapping(method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ProjectDto addNewProject(Principal principal, @RequestBody ProjectDto projectDto) {
        Optional<User> user = userService.getOneByUsername(principal.getName());

        if(!user.isPresent()) {
            throw new ApiException(ApiErrorEnum.USER_NOT_FOUND);
        }
        Project project = projectService.saveProject(user.get(), projectService.toModel(projectDto));
        return projectService.toDTO(project, true);
    }

    /**
     * Get a project by id
     *
     * @param id The id of the project
     * @return The project
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ProjectDto getOneById(@PathVariable("id") Long id) {
        Optional<Project> project = projectService.getOneById(id);
        if(!project.isPresent()) {
            throw new ApiException(ApiErrorEnum.PROJECT_NOT_FOUND);
        }

        return  projectService.toDTO(project.get(), true);
    }

    /**
     * Add a user to a project
     *
     * @param id Id of the project
     * @param usernameMap Username of the user to add
     * @return The project
     */
    @RequestMapping(value = "/{id}/users", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ProjectDto addUserToProject(@PathVariable("id") Long id,
                                       @RequestBody Map<String, String> usernameMap) {
        Optional<User> user = userService.getOneByUsername(usernameMap.get("username"));
        Optional<Project> project = projectService.getOneById(id);

        if(!user.isPresent()) {
            throw new ApiException(ApiErrorEnum.USER_NOT_FOUND);
        }
        if(!project.isPresent()) {
            throw new ApiException(ApiErrorEnum.PROJECT_NOT_FOUND);
        }

        projectService.saveProject(user.get(), project.get());
        return projectService.toDTO(project.get(), true);
    }

    /**
     * Delete a user from a dashboard
     *
     * @param id The project/dashboard id
     * @param userId The user id to delete
     * @return The project
     */
    @RequestMapping(value = "/{id}/users/{userId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ProjectDto deleteUserToProject(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        Optional<User> user = userService.getOne(userId);
        Optional<Project> project = projectService.getOneById(id);

        if(!user.isPresent()) {
            throw new ApiException(ApiErrorEnum.USER_NOT_FOUND);
        }
        if(!project.isPresent()) {
            throw new ApiException(ApiErrorEnum.PROJECT_NOT_FOUND);
        }

        projectService.deleteUserFromProject(user.get(), project.get());
        return projectService.toDTO(project.get(), true);
    }

    /**
     * Add widget into the dashboard
     *
     * @param id The project id
     * @param projectWidgetDto The projectWidget to add
     * @return The project
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ProjectDto addWidgetToProject(@PathVariable("id") Long id,
                                         @RequestBody ProjectWidgetDto projectWidgetDto) {
        ProjectWidget projectWidget = projectService.addWidgetToProject(projectWidgetDto);
        Optional<Project> project = projectService.getOneById(projectWidget.getProject().getId());

        if(!project.isPresent()) {
            throw new ApiException(ApiErrorEnum.PROJECT_NOT_FOUND);
        }

        return projectService.toDTO(project.get(), true);
    }
}
