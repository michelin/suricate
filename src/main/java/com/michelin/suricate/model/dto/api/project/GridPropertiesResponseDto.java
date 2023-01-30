package com.michelin.suricate.model.dto.api.project;

import com.michelin.suricate.model.dto.api.AbstractDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Properties of the dashboard for the related project")
public class GridPropertiesResponseDto extends AbstractDto {
    @Schema(description = "The number of columns in the dashboard", example = "1")
    private Integer maxColumn;

    @Schema(description = "The height in pixel of the widget", example = "1")
    private Integer widgetHeight;

    @Schema(description = "The css style of the dashboard grid")
    private String cssStyle;

}
