package io.suricate.monitoring.model.dto.api.project;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Grid properties for a dashboard
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ProjectGridResponse", description = "Properties of the dashboard for the related project")
public class GridPropertiesResponseDto extends AbstractDto {

    /**
     * Number of column in the dashboard
     */
    @ApiModelProperty(value = "The number of columns in the dashboard")
    private Integer maxColumn;

    /**
     * The height for widgets contained
     */
    @ApiModelProperty(value = "The height in pixel of the widget")
    private Integer widgetHeight;

    /**
     * The global css for the dashboard
     */
    @ApiModelProperty(value = "The css style of the dashboard grid")
    private String cssStyle;

}
