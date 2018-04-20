package io.suricate.monitoring.model.mapper.project;

import io.suricate.monitoring.model.dto.project.ProjectDto;
import io.suricate.monitoring.model.entity.project.Project;
import io.suricate.monitoring.model.mapper.role.UserMapper;
import io.suricate.monitoring.service.api.LibraryService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interface that manage the generation DTO/Model objects for project class
 */
@Component
@Mapper(
    componentModel = "spring",
    uses = {
        ProjectWidgetMapper.class,
        UserMapper.class
    }
)
public abstract class ProjectMapper {

    /**
     * The library service
     */
    @Autowired
    protected LibraryService libraryService;

    /* ******************************************************* */
    /*                  Simple Mapping                         */
    /* ******************************************************* */

    /**
     * Transform a project into a ProjectDto
     *
     * @param project The project to transform
     * @return The related project DTO
     */
    @Named("toProjectDtoDefault")
    @Mappings({
        @Mapping(target = "projectWidgets", source = "project.widgets", qualifiedByName = "toProjectWidgetDtosDefault"),
        @Mapping(target = "librariesToken", expression = "java(libraryService.getLibraries(project.getWidgets()))"),
        @Mapping(target = "users", qualifiedByName = "toUserDtosDefault")
    })
    public abstract ProjectDto toProjectDtoDefault(Project project);

    /**
     * Transform a project into a ProjectDto
     *
     * @param project The project to transform
     * @return The related project DTO
     */
    @Named("toProjectDtoWithoutProjectWidget")
    @Mappings({
        @Mapping(target = "projectWidgets", source = "project.widgets", ignore = true),
        @Mapping(target = "librariesToken", expression = "java(libraryService.getLibraries(project.getWidgets()))"),
        @Mapping(target = "users", qualifiedByName = "toUserDtosDefault")
    })
    public abstract ProjectDto toProjectDtoWithoutProjectWidget(Project project);

    /* ******************************************************* */
    /*                    List Mapping                         */
    /* ******************************************************* */

    /**
     * Tranform a list of projects into a list of projectDtos
     *
     * @param projects The list of project to tranform
     * @return The related list of dto object
     */
    @Named("toProjectDtosDefault")
    @IterableMapping(qualifiedByName = "toProjectDtoDefault")
    public abstract List<ProjectDto> toProjectDtosDefault(List<Project> projects);
}
