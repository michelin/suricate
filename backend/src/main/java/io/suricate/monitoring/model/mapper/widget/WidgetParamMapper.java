package io.suricate.monitoring.model.mapper.widget;

import io.suricate.monitoring.model.dto.widget.WidgetParamDto;
import io.suricate.monitoring.model.entity.widget.WidgetParam;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for Widget params class
 */
@Component
@Mapper(
    componentModel = "spring",
    uses = {
        WidgetParamValueMapper.class
    }
)
public abstract class WidgetParamMapper {

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a widgetParam into a widgetParamDto
     *
     * @param widgetParam The widgetParam to transform
     * @return The related widgetParam DTO
     */
    @Named("toWidgetParamDtoDefault")
    @Mappings({
        @Mapping(target = "values", source = "widgetParam.possibleValuesMap", qualifiedByName = "toWidgetParamValueDtosDefault")
    })
    public abstract WidgetParamDto toWidgetParamDtoDefault(WidgetParam widgetParam);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a list of widgetParams into a list of widgetParamDto
     *
     * @param widgetParams The list of widgetParams to transform
     * @return The related DTOs
     */
    @Named("toWidgetParamDtosDefault")
    @IterableMapping(qualifiedByName = "toWidgetParamDtoDefault")
    public abstract List<WidgetParamDto> toWidgetParamDtosDefault(List<WidgetParam> widgetParams);
}
