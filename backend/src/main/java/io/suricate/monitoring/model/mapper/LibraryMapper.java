package io.suricate.monitoring.model.mapper;

import io.suricate.monitoring.model.dto.LibraryDto;
import io.suricate.monitoring.model.entity.Library;
import io.suricate.monitoring.model.mapper.widget.WidgetMapper;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for library class
 */
@Component
@Mapper(
    componentModel = "spring",
    uses = {
        AssetMapper.class,
        WidgetMapper.class
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
        @Mapping(target = "widgets", qualifiedByName = "toWidgetDtosWithoutLibraries")
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
    @Named("toLibraryDtosDefault")
    @IterableMapping(qualifiedByName = "toLibraryDtoDefault")
    public abstract List<LibraryDto> toLibraryDtosDefault(List<Library> libraries);

    /**
     * Tranform a list of libraries into a list of librarieDto without widgets
     *
     * @param libraries The libraries to transform
     * @return The related list of libraries DTO
     */
    @Named("toLibraryDtosWithoutWidgets")
    @IterableMapping(qualifiedByName = "toLibraryDtoWithoutWidgets")
    public abstract List<LibraryDto> toLibraryDtosWithoutWidgets(List<Library> libraries);
}
