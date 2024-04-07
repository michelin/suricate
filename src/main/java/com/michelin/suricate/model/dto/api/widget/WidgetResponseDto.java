package com.michelin.suricate.model.dto.api.widget;

import com.michelin.suricate.model.dto.api.AbstractDto;
import com.michelin.suricate.model.dto.api.asset.AssetResponseDto;
import com.michelin.suricate.model.dto.api.category.CategoryResponseDto;
import com.michelin.suricate.model.enumeration.WidgetAvailabilityEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Widget response DTO.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Describe a widget")
public class WidgetResponseDto extends AbstractDto {
    @Schema(description = "Id", example = "1")
    private Long id;

    @Schema(description = "Widget name")
    private String name;

    @Schema(description = "Small description of this widget")
    private String description;

    @Schema(description = "Unique name to identify this widget")
    private String technicalName;

    @Schema(description = "Information on the usage of this widget")
    private String info;

    @Schema(description = "Delay between each execution of this widget")
    private Long delay;

    @Schema(description = "The css of this widget")
    private String cssContent;

    @Schema(description = "Timeout for Js execution (prevent infinity loop)", example = "30")
    private Long timeout;

    @Schema(description = "A representation by an image of the widget")
    private String imageToken;

    @Schema(description = "The image")
    private AssetResponseDto image;

    @Schema(description = "The category of this widget")
    private CategoryResponseDto category;

    @Schema(description = "The widget availabilities")
    private WidgetAvailabilityEnum widgetAvailability;

    @Schema(description = "The repository of this widget", example = "1")
    private Long repositoryId;

    @Schema(description = "The list of the params for this widget")
    private List<WidgetParamResponseDto> params;

    @Schema(description = "The library technical names")
    private List<String> libraryTechnicalNames;
}
