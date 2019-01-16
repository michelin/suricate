package io.suricate.monitoring.model.dto.api.configuration;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ConfigurationRequest", description = "Add or modify a configuration")
public class ConfigurationRequestDto extends AbstractDto {

    /**
     * The configuration value
     */
    @ApiModelProperty(value = "The configuration value")
    private String value;
}
