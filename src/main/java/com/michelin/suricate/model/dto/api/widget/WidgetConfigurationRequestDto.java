package com.michelin.suricate.model.dto.api.widget;

import com.michelin.suricate.model.dto.api.AbstractDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Widget configuration request DTO.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Add or modify a widget configuration")
public class WidgetConfigurationRequestDto extends AbstractDto {
    @Schema(description = "The configuration value")
    private String value;
}
