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

package com.michelin.suricate.services.mapper;

import com.michelin.suricate.model.dto.api.asset.AssetResponseDto;
import com.michelin.suricate.model.dto.api.export.ImportExportAssetDto;
import com.michelin.suricate.model.entities.Asset;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

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
     * Map an asset into an import export asset DTO
     * @param asset The asset to map
     * @return The import export asset as DTO
     */
    @Named("toImportExportAssetDTO")
    public abstract ImportExportAssetDto toImportExportAssetDTO(Asset asset);

    /**
     * Map an import export asset DTO as entity
     * @param importExportAssetDto The asset DTO to map
     * @return The asset as entity
     */
    @Named("toAssetEntity")
    public abstract Asset toAssetEntity(ImportExportAssetDto importExportAssetDto);
}
