package io.suricate.monitoring.model.dto.api.export;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.suricate.monitoring.model.enums.RepositoryTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Export object used to export repository data
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ImportExportRepositoryDto", description = "Export repository data")
public class ImportExportRepositoryDto extends AbstractDto {
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
    private boolean enabled;

    /**
     * The categories
     */
    @ApiModelProperty(value = "The categories", dataType = "java.util.List")
    private List<ImportExportCategoryDto> categories = new ArrayList<>();
}
