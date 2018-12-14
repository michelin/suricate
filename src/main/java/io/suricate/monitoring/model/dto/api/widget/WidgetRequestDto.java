package io.suricate.monitoring.model.dto.api.widget;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.suricate.monitoring.model.enums.WidgetAvailabilityEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Object representing a widget used for communication with clients of the webservice
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "WidgetRequest", description = "Describe a widget")
public class WidgetRequestDto extends AbstractDto {

    /**
     * The widget availability {@link WidgetAvailabilityEnum}
     */
    @ApiModelProperty(value = "The widget availabilities")
    private WidgetAvailabilityEnum widgetAvailability;
}