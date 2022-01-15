/*
 * Copyright 2012-2021 the original author or authors.
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

package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.project.ProjectRequestDto;
import io.suricate.monitoring.model.dto.api.project.ProjectResponseDto;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.services.api.LibraryService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manage the generation DTO/Model objects for project class
 */
@Component
@Mapper(
        componentModel = "spring",
        uses = {
            AssetMapper.class,
            ProjectGridMapper.class
        },
        imports = {
            Collection.class,
            Collectors.class
        })
public abstract class ProjectMapper {

    /**
     * The library service
     */
    @Autowired
    protected LibraryService libraryService;

    /**
     * Map a project into a DTO
     *
     * @param project The project to map
     * @return The project as DTO
     */
    @Named("toProjectDTO")
    @Mapping(target = "gridProperties.maxColumn", source = "project.maxColumn")
    @Mapping(target = "gridProperties.widgetHeight", source = "project.widgetHeight")
    @Mapping(target = "gridProperties.cssStyle", source = "project.cssStyle")
    @Mapping(target = "screenshotToken", expression = "java( project.getScreenshot() != null ? io.suricate.monitoring.utils.IdUtils.encrypt(project.getScreenshot().getId()) : null )")
    @Mapping(target = "librariesToken", expression = "java(libraryService.getLibrariesToken(project.getGrids().stream().map(ProjectGrid::getWidgets).flatMap(Collection::stream).collect(Collectors.toSet())))")
    @Mapping(target = "image", source = "project.screenshot", qualifiedByName = "toAssetDTO")
    @Mapping(target = "grids", qualifiedByName = "toProjectGridDTO")
    public abstract ProjectResponseDto toProjectDTO(Project project);

    /**
     * Map a project into a DTO
     * Ignore the libraries to not load all widgets
     *
     * @param project The project to map
     * @return The project as DTO
     */
    @Named("toProjectDTONoWidgets")
    @Mapping(target = "gridProperties.maxColumn", source = "project.maxColumn")
    @Mapping(target = "gridProperties.widgetHeight", source = "project.widgetHeight")
    @Mapping(target = "gridProperties.cssStyle", source = "project.cssStyle")
    @Mapping(target = "screenshotToken", expression = "java( project.getScreenshot() != null ? io.suricate.monitoring.utils.IdUtils.encrypt(project.getScreenshot().getId()) : null )")
    @Mapping(target = "image", source = "project.screenshot", qualifiedByName = "toAssetDTO")
    @Mapping(target = "grids", qualifiedByName = "toProjectGridDTO")
    public abstract ProjectResponseDto toProjectDTONoWidgets(Project project);

    /**
     * Map a project into a DTO without assets
     *
     * @param project The project to map
     * @return The project as DTO
     */
    @Named("toProjectNoAssetDTO")
    @Mapping(target = "gridProperties.maxColumn", source = "project.maxColumn")
    @Mapping(target = "gridProperties.widgetHeight", source = "project.widgetHeight")
    @Mapping(target = "gridProperties.cssStyle", source = "project.cssStyle")
    @Mapping(target = "grids", qualifiedByName = "toProjectGridDTO")
    public abstract ProjectResponseDto toProjectDTONoAsset(Project project);

    /**
     * Map a list of projects into a list of DTOs
     *
     * @param projects The list of project to map
     * @return The list of projects as DTOs
     */
    @Named("toProjectsDTOs")
    @IterableMapping(qualifiedByName = "toProjectDTONoWidgets")
    public abstract List<ProjectResponseDto> toProjectsDTOs(List<Project> projects);

    /**
     * Map a project DTO into a project entity
     *
     * @param projectRequestDto The project DTO to map
     * @return The project as entity
     */
    @Named("toProjectEntity")
    public abstract Project toProjectEntity(ProjectRequestDto projectRequestDto);
}
