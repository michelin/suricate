package io.suricate.monitoring.model.dto.api.projectgrid;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.suricate.monitoring.model.dto.api.projectwidget.ProjectWidgetResponseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ProjectGridResponseDto", description = "Describe a project grid")
public class ProjectGridResponseDto extends AbstractDto {
    /**
     * The project grid id
     */
    @ApiModelProperty(value = "The project grid id")
    private Long id;

    /**
     * The time
     */
    @ApiModelProperty(value = "The time")
    private Integer time;
}
