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
import io.suricate.monitoring.model.dto.project.ProjectWidgetRequest;
import io.suricate.monitoring.model.entity.user.User;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.suricate.monitoring.service.ProjectService;
import io.suricate.monitoring.service.UserService;
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

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    @Autowired
    public ProjectController(final ProjectService projectService, final UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<ProjectDto> getAll(Principal principal) {
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

        return projects.stream().map(project -> projectService.toDTO(project)).collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ProjectDto addNewProject(Principal principal, @RequestBody ProjectDto projectDto) {
        Optional<User> user = userService.getOneByUsername(principal.getName());

        if(!user.isPresent()) {
            throw new ApiException(ApiErrorEnum.USER_NOT_FOUND);
        }
        Project project = projectService.saveProject(user.get(), projectService.toModel(projectDto));
        return projectService.toDTO(project);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ProjectDto getOneById(@PathVariable("id") Long id) {
        Optional<Project> project = projectService.getOneById(id);
        if(!project.isPresent()) {
            throw new ApiException(ApiErrorEnum.PROJECT_NOT_FOUND);
        }

        return  projectService.toDTO(project.get());
    }

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
        return projectService.toDTO(project.get());
    }

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
        return projectService.toDTO(project.get());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ProjectDto addWidgetToProject(@PathVariable("id") Long id,
                                         @RequestBody ProjectWidgetRequest projectWidgetRequest) {
        ProjectWidget projectWidget = projectService.addWidgetToProject(projectWidgetRequest);
        Optional<Project> project = projectService.getOneById(projectWidget.getProject().getId());

        if(!project.isPresent()) {
            throw new ApiException(ApiErrorEnum.PROJECT_NOT_FOUND);
        }

        return projectService.toDTO(project.get());
    }
}
