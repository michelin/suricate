package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.dto.api.export.*;
import io.suricate.monitoring.model.entities.Category;
import io.suricate.monitoring.model.entities.Library;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.Repository;
import io.suricate.monitoring.services.mapper.CategoryMapper;
import io.suricate.monitoring.services.mapper.LibraryMapper;
import io.suricate.monitoring.services.mapper.ProjectMapper;
import io.suricate.monitoring.services.mapper.RepositoryMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Manage the import export
 */
@Service
public class ImportExportService {
    /**
     * The repository service
     */
    private final RepositoryService repositoryService;

    /**
     * The library service
     */
    private final LibraryService libraryService;

    /**
     * The category service
     */
    private final CategoryService categoryService;

    /**
     * The widget service
     */
    private final WidgetService widgetService;

    /**
     * The project service
     */
    private final ProjectService projectService;

    /**
     * The repository mapper
     */
    private final RepositoryMapper repositoryMapper;

    /**
     * The library mapper
     */
    private final LibraryMapper libraryMapper;

    /**
     * The category mapper
     */
    private final CategoryMapper categoryMapper;

    /**
     * The project mapper
     */
    private final ProjectMapper projectMapper;

    /**
     * Constructor
     * @param repositoryService The repository service
     * @param categoryService The category service
     * @param widgetService The widget service
     * @param libraryService The library service
     * @param projectService The project service
     * @param repositoryMapper The repository mapper
     * @param categoryMapper The category mapper
     * @param projectMapper The project mapper
     * @param libraryMapper The library mapper
     */
    public ImportExportService(RepositoryService repositoryService,
                               CategoryService categoryService,
                               WidgetService widgetService,
                               LibraryService libraryService,
                               ProjectService projectService,
                               RepositoryMapper repositoryMapper,
                               CategoryMapper categoryMapper,
                               ProjectMapper projectMapper,
                               LibraryMapper libraryMapper) {
        this.repositoryService = repositoryService;
        this.libraryService = libraryService;
        this.categoryService = categoryService;
        this.widgetService = widgetService;
        this.repositoryMapper = repositoryMapper;
        this.libraryMapper = libraryMapper;
        this.categoryMapper = categoryMapper;
        this.projectMapper = projectMapper;
        this.projectService = projectService;
    }

    /**
     * Get the application data to export
     * @return The application data to export
     */
    public ImportExportDto getDataToExport() {
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

        return exportDto;
    }

    /**
     * Import the application data
     * @param importDto The application data to import
     */
    @Transactional
    public void importData(ImportExportDto importDto) {
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
        importDto.getRepositories().forEach(repositoryDto ->
                repositoryDto.getCategories().forEach(categoryDto -> {
                    Category category = categoryMapper.toCategoryEntity(categoryDto);
                    categoryService.addOrUpdateCategory(category);

                    widgetService.addOrUpdateWidgets(category, libraries, repositories
                            .stream()
                            .filter(repository -> repository.getName().equals(repositoryDto.getName()))
                            .findFirst().orElse(null));
                }));

        // Import projects
        List<Project> projects = importDto.getProjects()
                .stream()
                .map(projectMapper::toProjectEntity)
                .collect(Collectors.toList());

        projectService.createUpdateProjects(projects);
    }
}
