package io.suricate.monitoring.model.dto.api.export;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Export object used to export library data
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ImportExportLibraryDto", description = "Export library data")
public class ImportExportLibraryDto extends AbstractDto {
    /**
     * The technical name
     */
    @ApiModelProperty(value = "The technical name")
    private String technicalName;

    /**
     * Asset of the library
     */
    @ApiModelProperty(value = "Asset of the library")
    private ImportExportAssetDto asset;
}
