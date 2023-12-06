package com.michelin.suricate.services.mapper;

import com.michelin.suricate.model.dto.api.export.ImportExportProjectDto;
import com.michelin.suricate.model.dto.api.projectgrid.ProjectGridRequestDto;
import com.michelin.suricate.model.dto.api.projectgrid.ProjectGridResponseDto;
import com.michelin.suricate.model.entities.Project;
import com.michelin.suricate.model.entities.ProjectGrid;
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
