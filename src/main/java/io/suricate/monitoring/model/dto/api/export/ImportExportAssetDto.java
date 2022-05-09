package io.suricate.monitoring.model.dto.api.export;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Export object used to export asset data
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ExportAssetDto", description = "Export asset data")
public class ImportExportAssetDto extends AbstractDto {
    /**
     * The blob content
     */
    @ApiModelProperty(value = "The blob content")
    private byte[] content;

    /**
     * The content type
     */
    @ApiModelProperty(value = "The content type")
    private String contentType;

    /**
     * The size of the asset
     */
    @ApiModelProperty(value = "The size of the asset")
    private long size;
}
