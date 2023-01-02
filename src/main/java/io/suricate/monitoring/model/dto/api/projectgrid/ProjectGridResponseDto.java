package io.suricate.monitoring.model.dto.api.projectgrid;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Describe a project grid")
public class ProjectGridResponseDto extends AbstractDto {
    @Schema(description = "The project grid id", example = "1")
    private Long id;

    @Schema(description = "The time", example = "30")
    private Integer time;
}
