package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.projectgrid.ProjectGridResponseDto;
import io.suricate.monitoring.model.entities.ProjectGrid;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

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
}
