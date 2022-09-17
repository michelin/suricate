package io.suricate.monitoring.model.dto.api.repository;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.suricate.monitoring.model.enums.RepositoryTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Describe a repository
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "RepositoryResponseDto", description = "Describe a widget repository")
public class RepositoryResponseDto extends AbstractDto {
    /**
     * The repository id
     */
    @ApiModelProperty(value = "The repository id", required = true, example = "1")
    private Long id;

    /**
     * The repository name
     */
    @ApiModelProperty(value = "The repository name", required = true)
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
     * The path of the repository in case of a local folder
     */
    @ApiModelProperty(value = "The path of the repository in case of a local folder")
    private String localPath;

    /**
     * The type of repository
     */
    @ApiModelProperty(value = "The type of repository")
    private RepositoryTypeEnum type;

    /**
     * If the repository is enabled or not
     */
    @ApiModelProperty(value = "True if the repository is enabled for update", required = true)
    private boolean enabled = true;

    /**
     * The priority order
     */
    @ApiModelProperty(value = "The priority order", required = true)
    private int priority;
}
