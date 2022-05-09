package io.suricate.monitoring.model.dto.api.export;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.suricate.monitoring.model.enums.DataTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Export object used to export category parameter data
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ImportExportCategoryParameterDto", description = "Export category parameter data")
public class ImportExportCategoryParameterDto extends AbstractDto {
    /**
     * The category parameter key
     */
    @ApiModelProperty(value = "The category parameter key")
    private String key;

    /**
     * The category parameter value
     */
    @ApiModelProperty(value = "The category parameter value")
    private String value;

    /**
     * The description
     */
    @ApiModelProperty(value = "The description")
    private String description;

    /**
     * Export value
     */
    @ApiModelProperty(value = "The export value")
    private boolean export;

    /**
     * The data type of the category parameter
     */
    @ApiModelProperty(value = "The category parameter data type")
    private DataTypeEnum dataType;
}
