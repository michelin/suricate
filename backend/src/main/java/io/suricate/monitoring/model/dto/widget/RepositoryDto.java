package io.suricate.monitoring.model.dto.widget;

import io.suricate.monitoring.model.enums.RepositoryTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * Describe a repository
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@ApiModel(value = "Repository", description = "Describe a widget repository")
public class RepositoryDto {
    /**
     * The repository name
     */
    @ApiModelProperty(value = "The repository name")
    private String name;

    /**
     * The repository url
     */
    @ApiModelProperty(value = "The repository url")
    private String url;

    /**
     * The repository branch to clone
     */
    @ApiModelProperty(value = "The repository branch to clone")
    private String branch;

    /**
     * If the repository is enable or not
     */
    @ApiModelProperty(value = "True if the repository is enabled for update")
    private boolean enabled = true;

    /**
     * The type of the repository
     */
    @ApiModelProperty(value = "The type of the repository")
    private RepositoryTypeEnum type;
}
