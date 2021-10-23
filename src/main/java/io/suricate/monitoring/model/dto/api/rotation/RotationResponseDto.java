package io.suricate.monitoring.model.dto.api.rotation;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.suricate.monitoring.model.dto.api.project.ProjectResponseDto;
import io.suricate.monitoring.model.dto.api.rotationproject.RotationProjectResponseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * The rotation response DTO
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "RotationResponse", description = "Describe a rotation response DTO")
public class RotationResponseDto extends AbstractDto {
    /**
     * The rotation id
     */
    @ApiModelProperty(value = "The rotation id")
    private Long id;

    /**
     * The rotation name
     */
    @ApiModelProperty(value = "The rotation name")
    private String name;

    /**
     * Does the progress bar should be displayed
     */
    @ApiModelProperty(value = "Does the progress bar should be displayed")
    private boolean progressBar;

    /**
     * The rotation token
     */
    @ApiModelProperty(value = "The rotation token")
    private String token;
}
