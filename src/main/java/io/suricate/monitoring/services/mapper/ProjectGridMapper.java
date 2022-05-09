package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.export.ImportExportProjectDto;
import io.suricate.monitoring.model.dto.api.projectgrid.ProjectGridRequestDto;
import io.suricate.monitoring.model.dto.api.projectgrid.ProjectGridResponseDto;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.ProjectGrid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring",
        uses = {
            ProjectWidgetMapper.class
        }
)
public abstract class ProjectGridMapper {
    /**
     * Map a project grid into a DTO
     *
     * @param projectGrid The project grid to map
     * @return The project grid as DTO
     */
    @Named("toProjectGridDTO")
    @Mapping(target = "widgets", ignore = true)
    public abstract ProjectGridResponseDto toProjectGridDTONoWidgets(ProjectGrid projectGrid);

    /**
     * Map a project grid into an import export project DTO
     * @param projectGrid The project grid to map
     * @return The project grid as DTO
     */
    @Named("toImportExportProjectGridDTO")
    @Mapping(target = "widgets", qualifiedByName = "toImportExportProjectWidgetDTO")
    public abstract ImportExportProjectDto.ImportExportProjectGridDto toImportExportProjectGridDTO(ProjectGrid projectGrid);

    /**
     * Map a project grid DTO into a project grid entity
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
     * Map a project grid DTO into a project grid entity
     *
     * @param projectGridRequestDto The project grid DTO
     * @param project The project DTO
     * @return The project grid as entity
     */
    @Named("toProjectGridEntity")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", source = "project")
    public abstract ProjectGrid toProjectGridEntity(ProjectGridRequestDto.GridRequestDto projectGridRequestDto, Project project);

    /**
     * Map a project grid DTO into a project grid entity
     *
     * @param importProjectGridRequestDto The imported project grid DTO from json file
     * @param project The project DTO
     * @return The project grid as entity
     */
    @Named("toProjectGridEntity")
    @Mapping(target = "widgets", qualifiedByName = "toProjectWidgetEntity")
    public abstract ProjectGrid toProjectGridEntity(ImportExportProjectDto.ImportExportProjectGridDto importProjectGridRequestDto);
}
