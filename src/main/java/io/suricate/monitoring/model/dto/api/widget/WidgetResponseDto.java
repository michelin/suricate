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
@ApiModel(value = "WidgetResponse", description = "Describe a widget")
public class WidgetResponseDto extends AbstractDto {

    /**
     * The widget ID
     */
    @ApiModelProperty(value = "Id")
    private Long id;

    /**
     * The widget name
     */
    @ApiModelProperty(value = "Widget name")
    private String name;

    /**
     * The widget description
     */
    @ApiModelProperty(value = "Small description of this widget")
    private String description;

    /**
     * The technical name
     */
    @ApiModelProperty(value = "Unique name to identifiy this widget")
    private String technicalName;

    /**
     * Information on the usage of this widget
     */
    @ApiModelProperty(value = "Information on the usage of this widget")
    private String info;

    /**
     * The delay for each execution of this widget
     */
    @ApiModelProperty(value = "Delay between each execution of this widget")
    private Long delay;

    /**
     * The timeout of the nashorn execution
     */
    @ApiModelProperty(value = "Timeout for nashorn execution (prevent infinity loop)")
    private Long timeout;

    /**
     * A representation by an image of the widget
     */
    @ApiModelProperty(value = "A representation by an image of the widget")
    private String imageToken;

    /**
     * The category of this widget
     */
    @ApiModelProperty(value = "The category of this widget")
    private Long categoryId;

    /**
     * The widget availability {@link WidgetAvailabilityEnum}
     */
    @ApiModelProperty(value = "The widget availabilities")
    private WidgetAvailabilityEnum widgetAvailability;

    /**
     * The repository used for this widget
     */
    @ApiModelProperty(value = "The repository of this widget")
    private Long repositoryId;
}