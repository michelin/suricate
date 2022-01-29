package io.suricate.monitoring.model.dto.api.projectgrid;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ProjectGridResponseDto", description = "Describe a project grid")
public class ProjectGridResponseDto {
    /**
     * The project grid id
     */
    @ApiModelProperty(value = "The project grid id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;

    /**
     * The time
     */
    @ApiModelProperty(value = "The time")
    private Integer time;
}
