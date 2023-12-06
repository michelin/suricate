package com.michelin.suricate.model.dto.api.export;

import com.michelin.suricate.model.dto.api.AbstractDto;
import com.michelin.suricate.model.dto.api.project.GridPropertiesResponseDto;
import com.michelin.suricate.model.dto.api.projectwidget.ProjectWidgetPositionResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Import export project DTO.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Import a project")
public class ImportExportProjectDto extends AbstractDto {
    @Schema(description = "The list of grids")
    List<ImportExportProjectGridDto> grids = new ArrayList<>();

    @Schema(description = "The project token", requiredMode = Schema.RequiredMode.REQUIRED)
    private String token;

    @Schema(description = "The project name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "In case of rotations, should the progress bar be displayed for the project")
    private boolean displayProgressBar;

    @Schema(description = "Image of the dashboard")
    private ImportExportAssetDto image;

    @Schema(description = "The grid properties", requiredMode = Schema.RequiredMode.REQUIRED)
    private GridPropertiesResponseDto gridProperties;

    /**
     * Import export project grid DTO.
     */
    @Data
    public static class ImportExportProjectGridDto {
        @Schema(description = "The list of grids")
        List<ImportExportProjectWidgetDto> widgets = new ArrayList<>();

        @Schema(description = "The ID", example = "1")
        private Long id;

        @Schema(description = "The time", example = "15")
        private Integer time;

        /**
         * Import export project widget DTO.
         */
        @Data
        public static class ImportExportProjectWidgetDto {
            @Schema(description = "The ID", example = "1")
            private Long id;

            @Schema(description = "The related widget technical name")
            private String widgetTechnicalName;

            @Schema(description = "The configuration of this widget")
            private String backendConfig;

            @Schema(description = "The position of the widget on the grid")
            private ProjectWidgetPositionResponseDto widgetPosition;
        }
    }
}
