package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.dto.api.export.*;
import io.suricate.monitoring.services.mapper.CategoryMapper;
import io.suricate.monitoring.services.mapper.ProjectMapper;
import io.suricate.monitoring.services.mapper.RepositoryMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Manage the import export
 */
@Service
public class ImportExportService {
    /**
     * The repository service
     */
    @Autowired
    private RepositoryService repositoryService;

    /**
     * The library service
     */
    @Autowired
    private LibraryService libraryService;

    /**
     * The category service
     */
    @Autowired
    private CategoryService categoryService;

    /**
     * The widget service
     */
    @Autowired
    private WidgetService widgetService;

    /**
     * The project service
     */
    @Autowired
    private ProjectService projectService;

    /**
     * The repository mapper
     */
    @Autowired
    private RepositoryMapper repositoryMapper;

    /**
     * The category mapper
     */
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * The project mapper
     */
    @Autowired
    private ProjectMapper projectMapper;

    /**
     * Import the application data
     * @param importDto The application data to import
     */
    @Transactional
    public void importData(ImportExportDto importDto) {
        // Import repositories
        /*List<Repository> repositories = importDto.getRepositories()
                .stream()
                .map(repositoryMapper::toRepositoryEntity)
                .collect(Collectors.toList());

        repositoryService.createUpdateRepositories(repositories);*/

        // Import libraries
        /*List<Library> libraries = importDto.getLibraries()
                .stream()
                .map(libraryMapper::toLibraryEntity)
                .collect(Collectors.toList());

        libraryService.createUpdateLibraries(libraries);*/

        // Import categories and widgets
        /*importDto.getRepositories().forEach(repositoryDto ->
                repositoryDto.getCategories().forEach(categoryDto -> {
                    Category category = categoryMapper.toCategoryEntity(categoryDto);
                    categoryService.addOrUpdateCategory(category);

                    widgetService.addOrUpdateWidgets(category, libraries, repositories
                            .stream()
                            .filter(repository -> repository.getName().equals(repositoryDto.getName()))
                            .findFirst().orElse(null));
                }));*/

        // Import projects
        /*List<Project> projects = importDto.getProjects()
                .stream()
                .map(projectMapper::toProjectEntity)
                .collect(Collectors.toList());

        projectService.createUpdateProjects(projects);*/
    }
}
