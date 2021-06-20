package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.rotationproject.RotationProjectRequestDto;
import io.suricate.monitoring.model.dto.api.rotationproject.RotationProjectResponseDto;
import io.suricate.monitoring.model.entities.RotationProject;
import io.suricate.monitoring.services.api.ProjectService;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Interface that manage the generation DTO/Model objects for rotation project class
 */
@Component
@Mapper(
        componentModel = "spring",
        uses = {
                ProjectMapper.class
        })
public abstract class RotationProjectMapper {
    /**
     * The project service
     */
    @Autowired
    protected ProjectService projectService;

    /**
     * Map a rotation project into a DTO
     *
     * @param rotationProject The rotation project to map
     * @return The rotation project as DTO
     */
    @Named("toRotationProjectDTO")
    @Mapping(target = "project", source = "project", qualifiedByName = "toProjectDTO")
    public abstract RotationProjectResponseDto toRotationProjectDTO(RotationProject rotationProject);

    /**
     * Map a rotation project DTO into a rotation project entity
     *
     * @param rotationProjectRequestDto The rotation project DTO to map
     * @return The rotation project as entity
     */
    @Named("toRotationProjectEntity")
    @Mapping(target = "project", expression = "java(projectService.getOneByToken(rotationProjectRequestDto.getProjectToken()).get())")
    public abstract RotationProject toRotationProjectEntity(RotationProjectRequestDto rotationProjectRequestDto);
}
