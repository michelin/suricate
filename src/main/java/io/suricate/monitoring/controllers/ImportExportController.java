package io.suricate.monitoring.controllers;

import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.api.export.ImportExportCategoryDto;
import io.suricate.monitoring.model.dto.api.export.ImportExportDto;
import io.suricate.monitoring.model.dto.api.export.ImportExportLibraryDto;
import io.suricate.monitoring.model.dto.api.export.ImportExportRepositoryDto;
import io.suricate.monitoring.model.dto.api.export.ImportExportProjectDto;
import io.suricate.monitoring.model.entities.Category;
import io.suricate.monitoring.model.entities.Library;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.Repository;
import io.suricate.monitoring.services.api.*;
import io.suricate.monitoring.services.mapper.CategoryMapper;
import io.suricate.monitoring.services.mapper.LibraryMapper;
import io.suricate.monitoring.services.mapper.ProjectMapper;
import io.suricate.monitoring.services.mapper.RepositoryMapper;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Export controller
 */
@RestController
@RequestMapping("/api")
@Api(value = "Export Controller", tags = {"Exports"})
public class ImportExportController {
    private final RepositoryService repositoryService;

    private final CategoryService categoryService;

    private final LibraryService libraryService;

    private final WidgetService widgetService;

    private final ProjectService projectService;

    private final RepositoryMapper repositoryMapper;

    private final CategoryMapper categoryMapper;

    private final LibraryMapper libraryMapper;

    private final ProjectMapper projectMapper;

    /**
     * Constructor
     */
    public ImportExportController(RepositoryService repositoryService,
                                  CategoryService categoryService,
                                  LibraryService libraryService,
                                  ProjectService projectService,
                                  WidgetService widgetService,
                                  RepositoryMapper repositoryMapper,
                                  LibraryMapper libraryMapper,
                                  ProjectMapper projectMapper,
                                  CategoryMapper categoryMapper) {
        this.repositoryService = repositoryService;
        this.categoryService = categoryService;
        this.projectService = projectService;
        this.libraryService = libraryService;
        this.widgetService = widgetService;
        this.repositoryMapper = repositoryMapper;
        this.categoryMapper = categoryMapper;
        this.libraryMapper = libraryMapper;
        this.projectMapper = projectMapper;
    }

    /**
     * Export the application data
     * @return The application data export
     */
    @ApiOperation(value = "Export the application data", response = ImportExportDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = String.class),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @PostMapping(value = "/v1/exports")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ImportExportDto> exports() {
        List<ImportExportRepositoryDto> importExportRepositories = repositoryService
                .getAll(StringUtils.EMPTY, Pageable.unpaged())
                .map(repositoryMapper::toImportExportRepositoryDTO)
                .getContent();

        List<Category> categories = categoryService
                .getAll(StringUtils.EMPTY, Pageable.unpaged())
                .getContent();

        importExportRepositories.forEach(repository -> {
            List<ImportExportCategoryDto> repositoryCategories =
                    categories
                            .stream()
                            .filter(category -> category.getWidgets()
                                    .stream()
                                    .anyMatch(widget -> widget.getRepository().getName().equals(repository.getName())))
                            .map(categoryMapper::toImportExportCategoryDTO)
                            .collect(Collectors.toList());

            repository.getCategories().addAll(repositoryCategories);
        });

        List<ImportExportLibraryDto> libraries = libraryService
                .getAll(StringUtils.EMPTY, Pageable.unpaged())
                .map(libraryMapper::toImportExportLibraryDTO)
                .getContent();


        List<ImportExportProjectDto> projects = projectService
                .getAll(StringUtils.EMPTY, Pageable.unpaged())
                .map(projectMapper::toImportExportProjectDTO)
                .getContent();

        ImportExportDto exportDto = new ImportExportDto();
        exportDto.setRepositories(importExportRepositories);
        exportDto.setLibraries(libraries);
        exportDto.setProjects(projects);

        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(exportDto);
    }

    /**
     * Import the application data
     * @return The application data export
     */
    @ApiOperation(value = "Import the application data", response = ImportExportDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = String.class),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class)
    })
    @PostMapping(value = "/v1/imports")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> imports(@ApiParam(name = "importDto", value = "The data to import", required = true)
                                             @RequestBody ImportExportDto importDto) {
        // Import repositories
        List<Repository> repositories = importDto.getRepositories()
                .stream()
                .map(repositoryMapper::toRepositoryEntity)
                .collect(Collectors.toList());

        repositoryService.createUpdateRepositories(repositories);

        // Import libraries
        List<Library> libraries = importDto.getLibraries()
                .stream()
                .map(libraryMapper::toLibraryEntity)
                .collect(Collectors.toList());

        libraryService.createUpdateLibraries(libraries);

        // Import categories and widgets
        importDto.getRepositories().forEach(repositoryDto -> {
            repositoryDto.getCategories().forEach(categoryDto -> {
                Category category = categoryMapper.toCategoryEntity(categoryDto);
                categoryService.addOrUpdateCategory(category);
                widgetService.addOrUpdateWidgets(category, libraries, repositories
                        .stream()
                        .filter(repository -> repository.getName().equals(repositoryDto.getName()))
                        .findFirst().orElse(null));
            });
        });

        // Import projects
        List<Project> projects = importDto.getProjects()
                .stream()
                .map(projectMapper::toProjectEntity)
                .collect(Collectors.toList());

        projectService.createUpdateProjects(projects);

        // todo: add widgets in repository request dto
        return ResponseEntity
                .ok()
                .build();
    }
}
