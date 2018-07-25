package io.suricate.monitoring.model.dto.widget;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    @ApiModelProperty(value = "The repository name", required = true)
    private String name;

    /**
     * The repository url
     */
    @ApiModelProperty(value = "The repository url", required = true)
    private String url;

    /**
     * The repository branch to clone
     */
    @ApiModelProperty(value = "The repository branch to clone", required = true)
    private String branch;

    /**
     * The login to use for the connection to the remote repository
     */
    @ApiModelProperty(value = "The login to use for the connection to the remote repository")
    private String login;

    /**
     * The password to use for the connection to the remote repository
     */
    @ApiModelProperty(value = "The password to use for the connection to the remote repository")
    private String password;

    /**
     * If the repository is enable or not
     */
    @ApiModelProperty(value = "True if the repository is enabled for update", required = true)
    private boolean enabled = true;

    /**
     * The list of related widgets
     */
    @ApiModelProperty(value = "The type of the repository")
    private List<WidgetDto> widgets = new ArrayList<>();
}
