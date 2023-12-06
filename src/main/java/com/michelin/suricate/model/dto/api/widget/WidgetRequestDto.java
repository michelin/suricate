package com.michelin.suricate.model.dto.api.widget;

import com.michelin.suricate.model.dto.api.AbstractDto;
import com.michelin.suricate.model.enums.WidgetAvailabilityEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Widget request DTO.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Describe a widget")
public class WidgetRequestDto extends AbstractDto {
    @Schema(description = "The widget availabilities")
    private WidgetAvailabilityEnum widgetAvailability;
}