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

package io.suricate.monitoring.model.mapper.project;

import io.suricate.monitoring.model.dto.project.ProjectDto;
import io.suricate.monitoring.model.entity.project.Project;
import io.suricate.monitoring.model.mapper.role.UserMapper;
import io.suricate.monitoring.service.api.LibraryService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for project class
 */
@Component
@Mapper(
    componentModel = "spring",
    uses = {
        ProjectWidgetMapper.class,
        UserMapper.class
    }
)
public abstract class ProjectMapper {

    /**
     * The library service
     */
    @Autowired
    protected LibraryService libraryService;

    /* ************************* TO DTO ********************************************** */

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Transform a project into a ProjectDto
     *
     * @param project The project to transform
     * @return The related project DTO
     */
    @Named("toProjectDtoDefault")
    @Mappings({
        @Mapping(target = "projectWidgets", source = "project.widgets", qualifiedByName = "toProjectWidgetDtosDefault"),
        @Mapping(target = "librariesToken", expression = "java(libraryService.getLibraries(project.getWidgets()))"),
        @Mapping(target = "users", qualifiedByName = "toUserDtosDefault")
    })
    public abstract ProjectDto toProjectDtoDefault(Project project);

    /**
     * Transform a project into a ProjectDto
     *
     * @param project The project to transform
     * @return The related project DTO
     */
    @Named("toProjectDtoWithoutProjectWidget")
    @Mappings({
        @Mapping(target = "projectWidgets", source = "project.widgets", ignore = true),
        @Mapping(target = "librariesToken", expression = "java(libraryService.getLibraries(project.getWidgets()))"),
        @Mapping(target = "users", qualifiedByName = "toUserDtosDefault")
    })
    public abstract ProjectDto toProjectDtoWithoutProjectWidget(Project project);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a list of projects into a list of projectDtos
     *
     * @param projects The list of project to tranform
     * @return The related list of dto object
     */
    @Named("toProjectDtosDefault")
    @IterableMapping(qualifiedByName = "toProjectDtoDefault")
    public abstract List<ProjectDto> toProjectDtosDefault(List<Project> projects);


    /* ************************* TO MODEL **************************************** */

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Transform a projectDto into a project when we want to add a new dashboard
     *
     * @param projectDto The project to transform
     * @return The related project domain object
     */
    @Named("toNewProject")
    @Mappings({
        @Mapping(target = "widgets", source = "projectDto.projectWidgets", ignore = true),
        @Mapping(target = "users", ignore = true)
    })
    public abstract Project toNewProject(ProjectDto projectDto);
}
