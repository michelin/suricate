package io.suricate.monitoring.model.mapper;

import io.suricate.monitoring.model.dto.AssetDto;
import io.suricate.monitoring.model.entity.Asset;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for asset class
 */
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
    public abstract AssetDto toAssetDtoDefault(Asset asset);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a list of assets into a list of assetDto
     *
     * @param assets The list of assets to transform
     * @return The related DTOs
     */
    @IterableMapping(qualifiedByName = "toAssetDtoDefault")
    public abstract List<AssetDto> toAssetDtos(List<Asset> assets);
}
