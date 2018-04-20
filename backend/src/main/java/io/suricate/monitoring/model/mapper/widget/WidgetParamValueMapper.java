package io.suricate.monitoring.model.mapper.widget;

import io.suricate.monitoring.model.dto.widget.WidgetParamValueDto;
import io.suricate.monitoring.model.entity.widget.WidgetParamValue;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for widgetParamValue class
 */
@Mapper(
    componentModel = "spring"
)
public abstract class WidgetParamValueMapper {

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a widgetParamValue into a widgetParamValueDto
     *
     * @param widgetParamValue The widgetParamValue to transform
     * @return The related widgetParamValue DTO
     */
    @Named("toWidgetParamValueDtoDefault")
    public abstract WidgetParamValueDto toWidgetParamValueDtoDefault(WidgetParamValue widgetParamValue);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a list of widgetParamValues into a list of widgetParamValueDto
     *
     * @param widgetParamValues The list of widgetParamValues to transform
     * @return The related DTOs
     */
    @IterableMapping(qualifiedByName = "toWidgetParamValueDtoDefault")
    public abstract List<WidgetParamValueDto> toWidgetParamValueDtos(List<WidgetParamValue> widgetParamValues);
}
