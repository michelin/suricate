package io.suricate.monitoring.model.dto.api.export;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Export asset data")
public class ImportExportAssetDto extends AbstractDto {
    @Schema(description = "The blob content")
    private byte[] content;

    @Schema(description = "The content type")
    private String contentType;

    @Schema(description = "The size of the asset", example = "1")
    private long size;
}
