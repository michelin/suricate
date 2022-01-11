package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.projectgrid.ProjectGridResponseDto;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.ProjectGrid;
import io.suricate.monitoring.services.api.ProjectService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ProjectGridMapper {
    /**
     * Map a project grid into a DTO
     *
     * @param projectGrid The project grid to map
     * @return The project grid as DTO
     */
    @Named("toProjectGridDTO")
    public abstract ProjectGridResponseDto toProjectGridDTO(ProjectGrid projectGrid);

    /**
     * Map a project grid DTO into a project grid entity
     *
     * @param projectToken The project DTO to map
     * @return The project as entity
     */
    @Named("toProjectGridEntity")
    @Mapping(target = "time", constant = "60")
    public abstract ProjectGrid toProjectGridEntity(Project project);
}
