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

package com.michelin.suricate.service.mapper;

import com.michelin.suricate.model.dto.api.export.ImportExportProjectDto;
import com.michelin.suricate.model.dto.api.project.ProjectRequestDto;
import com.michelin.suricate.model.dto.api.project.ProjectResponseDto;
import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.service.api.LibraryService;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Project mapper.
 */
@Component
@Mapper(
    componentModel = "spring",
    uses = {
        AssetMapper.class,
        ProjectGridMapper.class
    },
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ProjectMapper {
    @Autowired
    protected LibraryService libraryService;

    /**
     * Map a project into a DTO.
     *
     * @param project The project to map
     * @return The project as DTO
     */
    @Named("toProjectDto")
    @Mapping(target = "gridProperties.maxColumn", source = "project.maxColumn")
    @Mapping(target = "gridProperties.widgetHeight", source = "project.widgetHeight")
    @Mapping(target = "gridProperties.cssStyle", source = "project.cssStyle")
    @Mapping(target = "screenshotToken", expression = "java( project.getScreenshot() != null "
        + "? com.michelin.suricate.util.IdUtils.encrypt(project.getScreenshot().getId()) : null )")
    @Mapping(target = "librariesToken", expression = "java( libraryService.getLibraryTokensByProject(project) )")
    @Mapping(target = "image", source = "project.screenshot", qualifiedByName = "toAssetDto")
    @Mapping(target = "grids", qualifiedByName = "toProjectGridDto")
    public abstract ProjectResponseDto toProjectDto(Project project);

    /**
     * Map a project into a DTO
     * Ignore the libraries to not load all widgets.
     *
     * @param project The project to map
     * @return The project as DTO
     */
    @Named("toProjectDtoNoLibrary")
    @Mapping(target = "gridProperties.maxColumn", source = "project.maxColumn")
    @Mapping(target = "gridProperties.widgetHeight", source = "project.widgetHeight")
    @Mapping(target = "gridProperties.cssStyle", source = "project.cssStyle")
    @Mapping(target = "screenshotToken", expression = "java( project.getScreenshot() != null"
        + " ? com.michelin.suricate.util.IdUtils.encrypt(project.getScreenshot().getId()) : null )")
    @Mapping(target = "image", source = "project.screenshot", qualifiedByName = "toAssetDto")
    @Mapping(target = "grids", qualifiedByName = "toProjectGridDto")
    public abstract ProjectResponseDto toProjectDtoNoLibrary(Project project);

    /**
     * Map a project into a DTO without assets.
     *
     * @param project The project to map
     * @return The project as DTO
     */
    @Named("toProjectNoAssetDto")
    @Mapping(target = "gridProperties.maxColumn", source = "project.maxColumn")
    @Mapping(target = "gridProperties.widgetHeight", source = "project.widgetHeight")
    @Mapping(target = "gridProperties.cssStyle", source = "project.cssStyle")
    @Mapping(target = "grids", qualifiedByName = "toProjectGridDto")
    public abstract ProjectResponseDto toProjectDtoNoAsset(Project project);

    /**
     * Map a list of projects into a list of DTOs.
     *
     * @param projects The list of project to map
     * @return The list of projects as DTOs
     */
    @Named("toProjectsDtos")
    @IterableMapping(qualifiedByName = "toProjectDtoNoLibrary")
    public abstract List<ProjectResponseDto> toProjectsDtos(List<Project> projects);

    /**
     * Map a project DTO into a project entity.
     *
     * @param projectRequestDto The project DTO to map
     * @return The project as entity
     */
    @Named("toProjectEntity")
    public abstract Project toProjectEntity(ProjectRequestDto projectRequestDto);

    /**
     * Map an import project DTO into a project entity.
     *
     * @param importProjectRequestDto The import project DTO to map
     * @return The project as entity
     */
    @Named("toProjectEntity")
    @Mapping(target = "maxColumn", source = "gridProperties.maxColumn")
    @Mapping(target = "widgetHeight", source = "gridProperties.widgetHeight")
    @Mapping(target = "cssStyle", source = "gridProperties.cssStyle")
    @Mapping(target = "screenshot", source = "image", qualifiedByName = "toAssetEntity")
    @Mapping(target = "grids", qualifiedByName = "toProjectGridEntity")
    public abstract Project toProjectEntity(ImportExportProjectDto importProjectRequestDto);

    /**
     * Map a project into an import export project DTO.
     *
     * @param project The project to map
     * @return The project as DTO
     */
    @Named("toImportExportProjectDTO")
    @Mapping(target = "gridProperties.maxColumn", source = "project.maxColumn")
    @Mapping(target = "gridProperties.widgetHeight", source = "project.widgetHeight")
    @Mapping(target = "gridProperties.cssStyle", source = "project.cssStyle")
    @Mapping(target = "image", source = "project.screenshot", qualifiedByName = "toImportExportAssetDto")
    @Mapping(target = "grids", qualifiedByName = "toImportExportProjectGridDto")
    public abstract ImportExportProjectDto toImportExportProjectDto(Project project);
}
