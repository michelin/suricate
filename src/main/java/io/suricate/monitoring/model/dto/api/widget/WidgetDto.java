package io.suricate.monitoring.model.dto.api.widget;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.suricate.monitoring.model.dto.api.asset.AssetDto;
import io.suricate.monitoring.model.enums.WidgetAvailabilityEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Object representing a widget used for communication with clients of the webservice
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@ApiModel(value = "Widget", description = "Describe a widget")
public class WidgetDto extends AbstractDto {

    /**
     * The widget ID
     */
    @ApiModelProperty(value = "Table ID")
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
     * The html content of the widget
     */
    @ApiModelProperty(value = "The frontend html template")
    private String htmlContent;

    /**
     * The css content of the widget
     */
    @ApiModelProperty(value = "Global HTML for this widget")
    private String cssContent;

    /**
     * The JS of the widget (executed by nashorn
     */
    @ApiModelProperty(value = "JS template executed by the backend with Nashorn")
    private String backendJs;

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
    private AssetDto image;

    /**
     * The list of related libraries
     */
    @ApiModelProperty(value = "List of related JS libraries", dataType = "java.util.List")
    private List<LibraryDto> libraries = new ArrayList<>();

    /**
     * The category of this widget
     */
    @ApiModelProperty(value = "The category of this widget")
    private CategoryDto category;

    /**
     * The widget availability {@link WidgetAvailabilityEnum}
     */
    @ApiModelProperty(value = "The widget availabilities")
    private WidgetAvailabilityEnum widgetAvailability;

    /**
     * The repository used for this widget
     */
    @ApiModelProperty(value = "The repository of this widget")
    private RepositoryDto repository;

    /**
     * The list of the params for this widget
     */
    @ApiModelProperty(value = "List of params for this widget", dataType = "java.util.List")
    private List<WidgetParamDto> widgetParams = new ArrayList<>();
}
