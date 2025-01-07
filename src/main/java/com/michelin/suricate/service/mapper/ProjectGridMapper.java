/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.michelin.suricate.service.mapper;

import com.michelin.suricate.model.dto.api.export.ImportExportProjectDto;
import com.michelin.suricate.model.dto.api.projectgrid.ProjectGridRequestDto;
import com.michelin.suricate.model.dto.api.projectgrid.ProjectGridResponseDto;
import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.model.entity.ProjectGrid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * Project grid mapper.
 */
@Mapper(
    componentModel = "spring",
    uses = {
        ProjectWidgetMapper.class
    },
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ProjectGridMapper {
    /**
     * Map a project grid into a DTO.
     *
     * @param projectGrid The project grid to map
     * @return The project grid as DTO
     */
    @Named("toProjectGridDto")
    public abstract ProjectGridResponseDto toProjectGridDto(ProjectGrid projectGrid);

    /**
     * Map a project grid DTO into a project grid entity.
     *
     * @param projectGridRequestDto The project grid DTO
     * @param project               The project DTO
     * @return The project grid as entity
     */
    @Named("toProjectGridEntity")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", source = "project")
    public abstract ProjectGrid toProjectGridEntity(ProjectGridRequestDto.GridRequestDto projectGridRequestDto,
                                                    Project project);

    /**
     * Map a project grid DTO into a project grid entity.
     *
     * @param importProjectGridRequestDto The imported project grid DTO from json file
     * @return The project grid as entity
     */
    @Named("toProjectGridEntity")
    @Mapping(target = "widgets", qualifiedByName = "toProjectWidgetEntity")
    public abstract ProjectGrid toProjectGridEntity(
        ImportExportProjectDto.ImportExportProjectGridDto importProjectGridRequestDto);

    /**
     * Map a project into a project grid entity.
     *
     * @param project The project DTO
     * @return The project grid as entity
     */
    @Named("toProjectGridEntity")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "time", constant = "60")
    @Mapping(target = "project", source = "project")
    public abstract ProjectGrid toProjectGridEntity(Project project);

    /**
     * Map a project grid into an import export project DTO.
     *
     * @param projectGrid The project grid to map
     * @return The project grid as DTO
     */
    @Named("toImportExportProjectGridDto")
    @Mapping(target = "widgets", qualifiedByName = "toImportExportProjectWidgetDto")
    public abstract ImportExportProjectDto.ImportExportProjectGridDto toImportExportProjectGridDto(
        ProjectGrid projectGrid);
}
