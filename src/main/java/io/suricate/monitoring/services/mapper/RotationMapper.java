package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.project.ProjectResponseDto;
import io.suricate.monitoring.model.dto.api.rotation.RotationRequestDto;
import io.suricate.monitoring.model.dto.api.rotation.RotationResponseDto;
import io.suricate.monitoring.model.dto.api.rotationproject.RotationProjectRequestDto;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.Rotation;
import io.suricate.monitoring.model.entities.RotationProject;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for rotation class
 */
@Mapper(
        componentModel = "spring",
        uses = {
                RotationProjectMapper.class
        })
public abstract class RotationMapper {

    /**
     * Map a rotation into a DTO
     *
     * @param rotation The rotation to map
     * @return The rotation as DTO
     */
    @Named("toRotationDTO")
    public abstract RotationResponseDto toRotationDTO(Rotation rotation);

    /**
     * Map a list of rotations into a list of DTOs
     *
     * @param rotations The list of rotations to map
     * @return The list of rotations as DTOs
     */
    @Named("toRotationsDTOs")
    @IterableMapping(qualifiedByName = "toRotationDTO")
    public abstract List<RotationResponseDto> toRotationsDTOs(List<Rotation> rotations);

    /**
     * Map a rotation DTO into a rotation entity
     *
     * @param rotationRequestDto The rotation DTO to map
     * @return The rotation as entity
     */
    @Named("toRotationEntity")
    public abstract Rotation toRotationEntity(RotationRequestDto rotationRequestDto);
}
