package com.michelin.suricate.model.dto.api.category;

import com.michelin.suricate.model.dto.api.AbstractDto;
import com.michelin.suricate.model.enumeration.DataTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Category parameter response DTO.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Describe a category parameter")
public class CategoryParameterResponseDto extends AbstractDto {
    @Schema(description = "The category parameter key")
    private String key;

    @Schema(description = "The category parameter value")
    private String value;

    @Schema(description = "The description")
    private String description;

    @Schema(description = "The export value")
    private boolean export;

    @Schema(description = "The category parameter data type")
    private DataTypeEnum dataType;

    @Schema(description = "Related category for this config")
    private CategoryResponseDto category;
}
