package io.suricate.monitoring.model.dto.api.export;

import com.google.common.collect.Sets;
import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.suricate.monitoring.model.enums.DataTypeEnum;
import io.suricate.monitoring.model.enums.WidgetAvailabilityEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Lob;
import java.util.ArrayList;
import java.util.List;

/**
 * Export object used to export widget data
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ImportExportWidgetDto", description = "Export widget data")
public class ImportExportWidgetDto extends AbstractDto {
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
    @ApiModelProperty(value = "The html content of the widget")
    private String htmlContent;

    /**
     * The css content of the widget
     */
    @ApiModelProperty(value = "The css content of the widget")
    private String cssContent;

    /**
     * The JS of this widget
     */
    @ApiModelProperty(value = "The JS of this widget")
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
     * The widget availability {@link WidgetAvailabilityEnum}
     */
    @ApiModelProperty(value = "The widget availabilities")
    private WidgetAvailabilityEnum widgetAvailability;

    /**
     * The image
     */
    @ApiModelProperty(value = "The image")
    private ImportExportAssetDto image;

    /**
     * The list of the params for this widget
     */
    @ApiModelProperty(value = "The list of the params for this widget")
    private List<ImportExportWidgetParamDto> params;

    /**
     * The library technical names
     */
    @ApiModelProperty(value = "The library technical names")
    private List<String> libraryTechnicalNames;

    @Data
    @NoArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    @ApiModel(value = "ImportExportWidgetParamDto", description = "Export widget parameter data")
    public static class ImportExportWidgetParamDto {
        /**
         * The param name
         */
        @ApiModelProperty(value = "The param name", required = true)
        private String name;

        /**
         * The param description
         */
        @ApiModelProperty(value = "Describe how to set this param", required = true)
        private String description;

        /**
         * The default value of the param
         */
        @ApiModelProperty(value = "HTML Default value to insert on the field")
        private String defaultValue;

        /**
         * The param type {@link DataTypeEnum}
         */
        @ApiModelProperty(value = "The type of this param define the HTML element to display", required = true)
        private DataTypeEnum type;

        /**
         * The regex used for accept a file while uploading it if the type is a FILE
         */
        @ApiModelProperty(value = "A regex to respect for the field")
        private String acceptFileRegex;

        /**
         * An example of the usage of this param
         */
        @ApiModelProperty(value = "An example of the usage of this field")
        private String usageExample;


        /**
         * The usage tooltip of the parameter
         */
        @ApiModelProperty(value = "The usage tooltip of the parameter")
        private String usageTooltip;

        /**
         * If the param is required True by default
         */
        @ApiModelProperty(value = "If the field is required or not", required = true)
        private boolean required = true;

        /**
         * The list of param values if the type is COMBO or a MULTIPLE
         */
        @ApiModelProperty(value = "The list of possible values if the type is COMBO or MULTIPLE", dataType = "java.util.List")
        private List<ImportExportWidgetParamValueDto> values = new ArrayList<>();

        /**
         * Export object used to export widget parameter value data
         */
        @Data
        @NoArgsConstructor
        @EqualsAndHashCode(callSuper = false)
        @ApiModel(value = "ImportExportWidgetParamValueDto", description = "Export widget parameter value data")
        public static class ImportExportWidgetParamValueDto {
            /**
             * The key used in the js file
             */
            @ApiModelProperty(value = "The key used in the JS/HTML Template")
            private String jsKey;

            /**
             * The value of this param
             */
            @ApiModelProperty(value = "The user displayed value")
            private String value;
        }
    }
}
