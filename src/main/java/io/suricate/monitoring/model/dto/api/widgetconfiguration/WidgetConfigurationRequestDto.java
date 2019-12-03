package io.suricate.monitoring.model.dto.api.widgetconfiguration;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "Widget configuration request", description = "Add or modify a widget configuration")
public class WidgetConfigurationRequestDto extends AbstractDto {

    /**
     * The configuration value
     */
    @ApiModelProperty(value = "The configuration value")
    private String value;
}
