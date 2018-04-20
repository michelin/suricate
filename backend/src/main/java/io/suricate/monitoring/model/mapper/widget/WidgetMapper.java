package io.suricate.monitoring.model.mapper.widget;

import io.suricate.monitoring.model.dto.widget.WidgetDto;
import io.suricate.monitoring.model.entity.widget.Widget;
import io.suricate.monitoring.model.mapper.AssetMapper;
import io.suricate.monitoring.model.mapper.LibraryMapper;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for Widget class
 */
@Component
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
        @Mapping(target = "libraries", qualifiedByName = "toLibraryDtosWithoutWidgets"),
        @Mapping(target = "widgetParams", qualifiedByName = "toWidgetParamDtosDefault")
    })
    public abstract WidgetDto toWidgetDtoDefault(Widget widget);

    @Named("toWidgetDtoWithoutLibraries")
    @Mappings({
        @Mapping(target = "category", qualifiedByName = "toCategoryDtoWithoutWidgets"),
        @Mapping(target = "libraries", ignore = true),
        @Mapping(target = "widgetParams", qualifiedByName = "toWidgetParamDtosDefault")
    })
    public abstract WidgetDto toWidgetDtoWithoutLibraries(Widget widget);

    @Named("toWidgetDtoWithoutCategory")
    @Mappings({
        @Mapping(target = "category", ignore = true),
        @Mapping(target = "libraries", qualifiedByName = "toLibraryDtosWithoutWidgets"),
        @Mapping(target = "widgetParams", qualifiedByName = "toWidgetParamDtosDefault")
    })
    public abstract WidgetDto toWidgetDtoWithoutCategory(Widget widget);


    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a list of widgets into a list of widgetDto
     *
     * @param widgets The list of widget to transform
     * @return The related DTOs
     */
    @Named("toWidgetDtosDefault")
    @IterableMapping(qualifiedByName = "toWidgetDtoDefault")
    public abstract List<WidgetDto> toWidgetDtosDefault(List<Widget> widgets);

    /**
     * Tranform a list of widgets into a list of widgetDto without category
     *
     * @param widgets The list of widget to transform
     * @return The related DTOs
     */
    @Named("toWidgetDtosWithoutCategory")
    @IterableMapping(qualifiedByName = "toWidgetDtoWithoutCategory")
    public abstract List<WidgetDto> toWidgetDtosWithoutCategory(List<Widget> widgets);

    /**
     * Tranform a list of widgets into a list of widgetDto without libraries
     *
     * @param widgets The list of widget to transform
     * @return The related DTOs
     */
    @Named("toWidgetDtosWithoutLibraries")
    @IterableMapping(qualifiedByName = "toWidgetDtoWithoutLibraries")
    public abstract List<WidgetDto> toWidgetDtosWithoutLibraries(List<Widget> widgets);
}
