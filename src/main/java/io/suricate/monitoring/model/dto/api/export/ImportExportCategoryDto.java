package io.suricate.monitoring.model.dto.api.export;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Export object used to export category data
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ImportExportCategoryDto", description = "Export category data")
public class ImportExportCategoryDto extends AbstractDto {
    /**
     * The category name
     */
    @ApiModelProperty(value = "Category name")
    private String name;

    /**
     * The technical name of the category
     */
    @ApiModelProperty(value = "Category technical name, should be unique in table")
    private String technicalName;

    /**
     * The image
     */
    @ApiModelProperty(value = "The image")
    private ImportExportAssetDto image;

    /**
     * The category parameters
     */
    @ApiModelProperty(value = "Category parameters")
    private List<ImportExportCategoryParameterDto> categoryParameters;

    /**
     * The widgets
     */
    @ApiModelProperty(value = "The widgets", required = true)
    private List<ImportExportWidgetDto> widgets = new ArrayList<>();
}
