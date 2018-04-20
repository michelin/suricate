package io.suricate.monitoring.model.mapper.project;

import io.suricate.monitoring.model.dto.project.ProjectWidgetDto;
import io.suricate.monitoring.model.entity.project.ProjectWidget;
import io.suricate.monitoring.model.mapper.widget.WidgetMapper;
import io.suricate.monitoring.service.api.ProjectWidgetService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for project widget class
 */
@Component
@Mapper(
    componentModel = "spring",
    uses = {
        WidgetMapper.class,
        ProjectMapper.class
    }
)
public abstract class ProjectWidgetMapper {

    /**
     * The project widget service
     */
    @Autowired
    public ProjectWidgetService projectWidgetService;

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a project widget into a ProjectWidgetDto
     *
     * @param projectWidget The project widget to transform
     * @return The related project widget DTO
     */
    @Named("toProjectWidgetDtoDefault")
    @Mappings({
        @Mapping(target = "widgetPosition.col", source = "projectWidget.col"),
        @Mapping(target = "widgetPosition.row", source = "projectWidget.row"),
        @Mapping(target = "widgetPosition.height", source = "projectWidget.height"),
        @Mapping(target = "widgetPosition.width", source = "projectWidget.width"),
        @Mapping(target = "instantiateHtml", expression = "java(projectWidgetService.instantiateProjectWidgetHtml(projectWidget))"),
        @Mapping(target = "project", qualifiedByName = "toProjectDtoWithoutProjectWidget")
    })
    public abstract ProjectWidgetDto toProjectWidgetDtoDefault(ProjectWidget projectWidget);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a list of project widgets into a list of projectWidgetDtos
     *
     * @param projectWidgets The list of project widget to tranform
     * @return The related list of dto object
     */
    @Named("toProjectWidgetDtosDefault")
    @IterableMapping(qualifiedByName = "toProjectWidgetDtoDefault")
    public abstract List<ProjectWidgetDto> toProjectWidgetDtosDefault(List<ProjectWidget> projectWidgets);
}
