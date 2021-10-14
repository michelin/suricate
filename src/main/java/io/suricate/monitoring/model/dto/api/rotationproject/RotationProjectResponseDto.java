package io.suricate.monitoring.model.dto.api.rotationproject;

import io.suricate.monitoring.model.dto.api.project.ProjectResponseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * The rotation project response DTO
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "RotationProjectResponse", description = "Describe a rotation project response DTO")
public class RotationProjectResponseDto {
    /**
     * The rotation speed of the project
     */
    @ApiModelProperty(value = "The rotation speed of the project")
    private int rotationSpeed;

    /**
     * The project response DTO
     */
    @ApiModelProperty(value = "The project response DTO")
    private ProjectResponseDto project;
}
