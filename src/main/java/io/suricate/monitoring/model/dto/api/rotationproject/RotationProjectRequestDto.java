package io.suricate.monitoring.model.dto.api.rotationproject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * Rotation project request DTO
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "RotationProjectRequest", description = "Hold the project information of a rotation")
public class RotationProjectRequestDto {

    /**
     * The project token
     */
    @ApiModelProperty(value = "The project token")
    private String projectToken;

    /**
     * The rotation speed of the project
     */
    @ApiModelProperty(value = "The rotation speed of the project")
    private int rotationSpeed;
}
