package io.suricate.monitoring.model.mapper.widget;

import io.suricate.monitoring.model.dto.widget.WidgetDto;
import io.suricate.monitoring.model.entity.widget.Widget;
import io.suricate.monitoring.model.mapper.AssetMapper;
import io.suricate.monitoring.model.mapper.LibraryMapper;
import org.mapstruct.*;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for Widget class
 */
@Mapper(
    componentModel = "spring",
    uses = {
        AssetMapper.class,
        LibraryMapper.class,
        CategoryMapper.class,
        WidgetParamMapper.class
    }
)
public abstract class WidgetMapper {

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a Widget into a WidgetDto
     *
     * @param widget The widget to transform
     * @return The related widget DTO
     */
    @Named("toWidgetDtoDefault")
    @Mappings({
        @Mapping(target = "category", qualifiedByName = "toCategoryDtoWithoutWidgets"),
        @Mapping(target = "libraries", qualifiedByName = "toLibraryDtoWithoutWidgets")
    })
    public abstract WidgetDto toWidgetDtoDefault(Widget widget);

    @Named("toWidgetDtoWithoutLibraries")
    @Mappings({
        @Mapping(target = "category", qualifiedByName = "toCategoryDtoWithoutWidgets"),
        @Mapping(target = "libraries", ignore = true)
    })
    public abstract WidgetDto toWidgetDtoWithoutLibraries(Widget widget);


    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a list of widgets into a list of widgetDto
     *
     * @param widgets The list of widget to transform
     * @return The related DTOs
     */
    @IterableMapping(qualifiedByName = "toWidgetDtoDefault")
    public abstract List<WidgetDto> toWidgetDtos(List<Widget> widgets);

}
