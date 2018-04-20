package io.suricate.monitoring.model.mapper;

import io.suricate.monitoring.model.dto.LibraryDto;
import io.suricate.monitoring.model.entity.Library;
import org.mapstruct.*;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for library class
 */
@Mapper(
    componentModel = "spring",
    uses = {
        AssetMapper.class
    }
)
public abstract class LibraryMapper {

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a library into a libraryDto
     *
     * @param library The library to transform
     * @return The related library DTO
     */
    @Named("toLibraryDtoDefault")
    @Mappings({
        @Mapping(target = "widgets", qualifiedByName = "toWidgetDtoWithoutLibraries")
    })
    public abstract LibraryDto toLibraryDtoDefault(Library library);

    /**
     * Tranform a library into a libraryDto
     *
     * @param library The library to transform
     * @return The related library DTO
     */
    @Named("toLibraryDtoWithoutWidgets")
    @Mappings({
        @Mapping(target = "widgets", ignore = true)
    })
    public abstract LibraryDto toLibraryDtoWithoutWidgets(Library library);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a list of libraries into a list of librarieDto
     *
     * @param libraries The libraries to transform
     * @return The related list of libraries DTO
     */
    @IterableMapping(qualifiedByName = "toLibraryDtoDefault")
    public abstract List<LibraryDto> toLibraryDtos(List<Library> libraries);
}
