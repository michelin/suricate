package com.michelin.suricate.model.dto.api.export;

import com.michelin.suricate.model.dto.api.AbstractDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Export application data")
public class ImportExportDto extends AbstractDto {
    @Schema(description = "The repositories", type = "java.util.List")
    private List<ImportExportRepositoryDto> repositories = new ArrayList<>();

    @Schema(description = "The projects", type = "java.util.List")
    private List<ImportExportProjectDto> projects = new ArrayList<>();
}
