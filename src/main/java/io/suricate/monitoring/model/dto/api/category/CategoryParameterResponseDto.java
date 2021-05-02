package io.suricate.monitoring.model.dto.api.category;

import io.suricate.monitoring.model.enums.DataTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Represent a category parameters response used for communication with the clients via webservices
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "CategoryParamResponse", description = "Describe a category parameter")
public class CategoryParameterResponseDto {

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
     * Export value
     */
    @ApiModelProperty(value = "The export value")
    private boolean export;

    /**
     * The data type of the category parameter
     */
    @ApiModelProperty(value = "The category parameter data type")
    private DataTypeEnum dataType;

    /**
     * Make a link between category and configurations
     */
    @ApiModelProperty(value = "Related category for this config")
    private CategoryResponseDto category;
}
