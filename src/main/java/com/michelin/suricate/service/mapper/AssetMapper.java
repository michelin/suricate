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

package com.michelin.suricate.service.mapper;

import com.michelin.suricate.model.dto.api.asset.AssetResponseDto;
import com.michelin.suricate.model.dto.api.export.ImportExportAssetDto;
import com.michelin.suricate.model.entity.Asset;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

/**
 * Asset mapper.
 */
@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class AssetMapper {
    /**
     * Map an asset into an asset DTO.
     *
     * @param asset The asset to map
     * @return The asset as DTO
     */
    @Named("toAssetDto")
    public abstract AssetResponseDto toAssetDto(Asset asset);

    /**
     * Map an asset into an import export asset DTO.
     *
     * @param asset The asset to map
     * @return The import export asset as DTO
     */
    @Named("toImportExportAssetDto")
    public abstract ImportExportAssetDto toImportExportAssetDto(Asset asset);

    /**
     * Map an import export asset DTO as entity.
     *
     * @param importExportAssetDto The asset DTO to map
     * @return The asset as entity
     */
    @Named("toAssetEntity")
    public abstract Asset toAssetEntity(ImportExportAssetDto importExportAssetDto);
}
