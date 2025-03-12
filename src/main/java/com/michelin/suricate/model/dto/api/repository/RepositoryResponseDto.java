/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.michelin.suricate.model.dto.api.repository;

import com.michelin.suricate.model.dto.api.AbstractDto;
import com.michelin.suricate.model.enumeration.RepositoryTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/** Repository response DTO. */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Describe a widget repository")
public class RepositoryResponseDto extends AbstractDto {
    @Schema(description = "The repository id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

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
    private boolean enabled = true;

    @Schema(description = "The priority order", requiredMode = Schema.RequiredMode.REQUIRED)
    private int priority;

    @Schema(description = "The repository creation date", requiredMode = Schema.RequiredMode.REQUIRED)
    private Date createdDate;
}
