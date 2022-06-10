package io.suricate.monitoring.model.dto.api.export;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Export object used to export application data
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ImportExportDto", description = "Export application data")
public class ImportExportDto extends AbstractDto {
    /**
     * The repositories
     */
    @ApiModelProperty(value = "The repositories", dataType = "java.util.List")
    private List<ImportExportRepositoryDto> repositories = new ArrayList<>();

    /**
     * The projects
     */
    @ApiModelProperty(value = "The projects", dataType = "java.util.List")
    private List<ImportExportProjectDto> projects = new ArrayList<>();
}
