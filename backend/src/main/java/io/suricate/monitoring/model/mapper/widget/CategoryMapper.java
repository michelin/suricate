package io.suricate.monitoring.model.mapper.widget;

import io.suricate.monitoring.model.dto.widget.CategoryDto;
import io.suricate.monitoring.model.entity.widget.Category;
import io.suricate.monitoring.model.mapper.AssetMapper;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for Category class
 */
@Mapper(
    componentModel = "spring",
    uses = {
        AssetMapper.class,
        WidgetMapper.class
    }
)
public abstract class CategoryMapper {

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a Category into a CategoryDto
     *
     * @param category The category to transform
     * @return The related category DTO
     */
    @Named("toCategoryDtoDefault")
    public abstract CategoryDto toCategoryDtoDefault(Category category);

    /**
     * Tranform a Category into a CategoryDto
     *
     * @param category The category to transform
     * @return The related category DTO
     */
    @Named("toCategoryDtoWithoutWidgets")
    @Mappings({
        @Mapping(target = "widgets", ignore = true)
    })
    public abstract CategoryDto toCategoryDtoWithoutWidgets(Category category);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a list of categories into a list of categoryDto
     *
     * @param categories The list of category to transform
     * @return The related DTO
     */
    @IterableMapping(qualifiedByName = "toCategoryDtoDefault")
    public abstract List<CategoryDto> toCategoryDtos(List<Category> categories);
}
