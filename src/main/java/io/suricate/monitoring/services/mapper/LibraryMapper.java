package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.export.ImportExportLibraryDto;
import io.suricate.monitoring.model.dto.api.export.ImportExportRepositoryDto;
import io.suricate.monitoring.model.entities.Library;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Manage the generation DTO/Model objects for Library class
 */
@Mapper(componentModel = "spring",
        uses = {
                AssetMapper.class
        })
public abstract class LibraryMapper {
    /**
     * Map a library into an import export DTO
     *
     * @param library The library to map
     * @return The import export library as DTO
     */
    @Named("toImportExportLibraryDTO")
    @Mapping(target = "asset", source = "library.asset", qualifiedByName = "toImportExportAssetDTO")
    public abstract ImportExportLibraryDto toImportExportLibraryDTO(Library library);

    /**
     * Map an import export library into an entity.
     * @param importExportLibraryDto The library to map
     * @return The library as entity
     */
    @Named("toLibraryEntity")
    @Mapping(target = "asset", qualifiedByName = "toAssetEntity")
    public abstract Library toLibraryEntity(ImportExportLibraryDto importExportLibraryDto);
}
