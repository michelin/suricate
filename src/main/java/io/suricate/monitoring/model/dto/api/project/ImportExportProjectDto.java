package io.suricate.monitoring.model.dto.api.project;

import io.suricate.monitoring.model.dto.api.asset.AssetResponseDto;
import io.suricate.monitoring.model.dto.api.projectwidget.ProjectWidgetPositionRequestDto;
import io.suricate.monitoring.model.dto.api.projectwidget.ProjectWidgetPositionResponseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Project object used to import a new project from file
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ImportProjectRequestDto", description = "Import a project")
public class ImportExportProjectDto {
    /**
     * The project name
     */
    @ApiModelProperty(value = "The project name", required = true)
    private String name;

    /**
     * In case of rotations, should the progress bar be displayed for the project
     */
    @ApiModelProperty(value = "In case of rotations, should the progress bar be displayed for the project")
    private boolean displayProgressBar;

    /**
     * Image of the dashboard
     */
    @ApiModelProperty(value = "Image of the dashboard")
    private AssetResponseDto image;

    /**
     * The grid properties
     */
    @ApiModelProperty(value = "The grid properties", required = true)
    private GridPropertiesResponseDto gridProperties;

    /**
     * The list of grids
     */
    @ApiModelProperty(value = "The list of grids")
    List<ImportExportProjectGridDto> grids;

    @Data
    public static class ImportExportProjectGridDto {
        /**
         * The time
         */
        @ApiModelProperty(value = "The time")
        private Integer time;

        /**
         * The widgets to import
         */
        @ApiModelProperty(value = "The list of grids")
        List<ImportExportProjectWidgetDto> widgets;

        @Data
        public static class ImportExportProjectWidgetDto {
            /**
             * The related widget technical name
             */
            @ApiModelProperty(value = "The related widget technical name")
            private String widgetTechnicalName;

            /**
             * Contains the configuration of the widget
             */
            @ApiModelProperty(value = "The configuration of this widget")
            private String backendConfig;

            /**
             * The position of this instance of widget in the grid
             */
            @ApiModelProperty(value = "The position of the widget on the grid")
            private ProjectWidgetPositionResponseDto widgetPosition;
        }
    }
}
