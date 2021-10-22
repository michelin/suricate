package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.rotationproject.RotationProjectRequestDto;
import io.suricate.monitoring.model.dto.api.rotationproject.RotationProjectResponseDto;
import io.suricate.monitoring.model.entities.Rotation;
import io.suricate.monitoring.model.entities.RotationProject;
import io.suricate.monitoring.services.api.ProjectService;
import io.suricate.monitoring.services.api.RotationService;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
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
     * The rotation service
     */
    @Autowired
    protected RotationService rotationService;

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
     * Map a list of rotation project into a list of DTOs
     *
     * @param rotationProjects The list of rotation project to map
     * @return The list of rotation project as DTOs
     */
    @Named("toRotationProjectDTOs")
    @IterableMapping(qualifiedByName = "toRotationProjectDTO")
    public abstract List<RotationProjectResponseDto> toRotationProjectDTOs(Collection<RotationProject> rotationProjects);

    /**
     * Map a rotation project DTO into a rotation project entity
     *
     * @param rotationProjectRequestDto The rotation project DTO to map
     * @return The rotation project as entity
     */
    @Named("toRotationProjectEntity")
    @Mapping(target = "rotation", expression = "java(rotationService.getOneByToken(rotationToken).get())")
    @Mapping(target = "project", expression = "java(projectService.getOneByToken(rotationProjectRequestDto.getProjectToken()).get())")
    public abstract RotationProject toRotationProjectEntity(RotationProjectRequestDto rotationProjectRequestDto, String rotationToken);
}
