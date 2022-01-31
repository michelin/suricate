/*
 *
 *  * Copyright 2012-2021 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.asset.AssetResponseDto;
import io.suricate.monitoring.model.dto.api.project.ProjectRequestDto;
import io.suricate.monitoring.model.entities.Asset;
import io.suricate.monitoring.model.entities.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Manage the generation DTO/Model objects for asset class
 */
@Component
@Mapper(componentModel = "spring")
public abstract class AssetMapper {
    /**
     * Map an asset into an asset DTO
     *
     * @param asset The asset to map
     * @return The asset as DTO
     */
    @Named("toAssetDTO")
    public abstract AssetResponseDto toAssetDTO(Asset asset);

    /**
     * Map an asset into an asset DTO for export
     *
     * @param asset The asset to map
     * @return The asset as DTO
     */
    @Named("toExportAssetDTO")
    @Mapping(target = "id", ignore = true)
    public abstract AssetResponseDto toExportAssetDTO(Asset asset);
}
