package io.suricate.monitoring.model.dto.api.export;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.suricate.monitoring.model.enums.RepositoryTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Export repository data")
public class ImportExportRepositoryDto extends AbstractDto {
    @Schema(description = "The repository name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "The repository url")
    private String url;

    @Schema(description = "The repository branch to clone")
    private String branch;

    @Schema(description = "The login to use for the connection to the remote repository")
    private String login;

    @Schema(description = "The password to use for the connection to the remote repository")
    private String password;

    @Schema(description = "The path of the repository in case of a local folder")
    private String localPath;

    @Schema(description = "The type of repository")
    private RepositoryTypeEnum type;

    @Schema(description = "True if the repository is enabled for update", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean enabled;
}
