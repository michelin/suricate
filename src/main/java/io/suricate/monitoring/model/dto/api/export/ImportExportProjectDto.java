package io.suricate.monitoring.model.dto.api.export;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.suricate.monitoring.model.dto.api.asset.AssetResponseDto;
import io.suricate.monitoring.model.dto.api.export.ImportExportAssetDto;
import io.suricate.monitoring.model.dto.api.project.GridPropertiesResponseDto;
import io.suricate.monitoring.model.dto.api.projectwidget.ProjectWidgetPositionRequestDto;
import io.suricate.monitoring.model.dto.api.projectwidget.ProjectWidgetPositionResponseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

/**
 * Project object used to import a new project from file
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ImportProjectRequestDto", description = "Import a project")
public class ImportExportProjectDto extends AbstractDto {
    /**
     * The project token
     */
    @ApiModelProperty(value = "The project token", required = true)
    private String token;

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
    private ImportExportAssetDto image;

    /**
     * The grid properties
     */
    @ApiModelProperty(value = "The grid properties", required = true)
    private GridPropertiesResponseDto gridProperties;

    /**
     * The list of grids
     */
    @ApiModelProperty(value = "The list of grids")
    List<ImportExportProjectGridDto> grids = new ArrayList<>();

    @Data
    public static class ImportExportProjectGridDto {
        /**
         * The ID
         */
        @ApiModelProperty(value = "The ID")
        private Long id;

        /**
         * The time
         */
        @ApiModelProperty(value = "The time")
        private Integer time;

        /**
         * The widgets to import
         */
        @ApiModelProperty(value = "The list of grids")
        List<ImportExportProjectWidgetDto> widgets = new ArrayList<>();

        @Data
        public static class ImportExportProjectWidgetDto {
            /**
             * The ID
             */
            @ApiModelProperty(value = "The ID")
            private Long id;

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
