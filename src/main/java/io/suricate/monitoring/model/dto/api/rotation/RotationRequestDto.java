package io.suricate.monitoring.model.dto.api.rotation;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.suricate.monitoring.model.dto.api.rotationproject.RotationProjectRequestDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Rotation request DTO
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "RotationRequest", description = "Create a rotation")
public class RotationRequestDto extends AbstractDto {
    /**
     * The rotation name
     */
    @ApiModelProperty(value = "The rotation name")
    private String name;
}
