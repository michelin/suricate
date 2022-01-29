package io.suricate.monitoring.model.dto.api.project;

import io.suricate.monitoring.model.dto.api.projectgrid.ProjectGridRequestDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Project object used to import a new project from file
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ImportProjectRequestDto", description = "Import a project")
public class ImportProjectRequestDto {
    /**
     * The project name
     */
    @ApiModelProperty(value = "The project name", required = true)
    private String name;

    /**
     * The grid properties
     */
    @ApiModelProperty(value = "The grid properties", required = true)
    private ImportProjectGridPropertiesRequestDto gridProperties;

    /**
     * In case of rotations, should the progress bar be displayed for the project
     */
    @ApiModelProperty(value = "In case of rotations, should the progress bar be displayed for the project")
    private boolean displayProgressBar;

    /**
     * The list of grids
     */
    @ApiModelProperty(value = "The list of grids")
    List<ProjectGridRequestDto.GridRequestDto> grids;

    @Data
    public static class ImportProjectGridPropertiesRequestDto {
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
}
