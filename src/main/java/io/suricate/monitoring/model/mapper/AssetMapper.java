/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.suricate.monitoring.model.mapper;

import io.suricate.monitoring.model.dto.api.asset.AssetResponseDto;
import io.suricate.monitoring.model.entity.Asset;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for asset class
 */
@Component
@Mapper(
    componentModel = "spring"
)
public abstract class AssetMapper {


    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a Asset into a assetDto
     *
     * @param asset The asset to transform
     * @return The related asset DTO
     */
    @Named("toAssetDtoDefault")
    public abstract AssetResponseDto toAssetDtoDefault(Asset asset);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a list of assets into a list of assetDto
     *
     * @param assets The list of assets to transform
     * @return The related DTOs
     */
    @Named("toAssetDtosDefault")
    @IterableMapping(qualifiedByName = "toAssetDtoDefault")
    public abstract List<AssetResponseDto> toAssetDtosDefault(List<Asset> assets);
}
