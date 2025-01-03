package com.michelin.suricate.service.git;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entity.Library;
import com.michelin.suricate.model.entity.Repository;
import com.michelin.suricate.model.enumeration.RepositoryTypeEnum;
import com.michelin.suricate.property.ApplicationProperties;
import com.michelin.suricate.service.api.CategoryService;
import com.michelin.suricate.service.api.LibraryService;
import com.michelin.suricate.service.api.RepositoryService;
import com.michelin.suricate.service.api.WidgetService;
import com.michelin.suricate.service.cache.CacheService;
import com.michelin.suricate.service.js.scheduler.JsExecutionScheduler;
import com.michelin.suricate.service.websocket.DashboardWebSocketService;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GitServiceTest {
    @Mock
    private JsExecutionScheduler jsExecutionScheduler;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private WidgetService widgetService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private LibraryService libraryService;

    @Mock
    private RepositoryService repositoryService;

    @Mock
    private DashboardWebSocketService dashboardWebSocketService;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private GitService gitService;

    @Test
    void shouldUpdateWidgetFromEnabledGitRepositoriesAsync() {
        ApplicationProperties.Widgets widgetsProperties = new ApplicationProperties.Widgets();
        widgetsProperties.setUpdateEnable(true);

        Library library = new Library();
        library.setTechnicalName("test.js");

        Repository repository = new Repository();
        repository.setId(1L);
        repository.setName("repository");
        repository.setBranch("master");
        repository.setType(RepositoryTypeEnum.LOCAL);
        repository.setLocalPath("src/test/resources/repository");

        when(applicationProperties.getWidgets())
            .thenReturn(widgetsProperties);
        when(repositoryService.findAllByEnabledOrderByPriorityDescCreatedDateAsc(true))
            .thenReturn(Optional.of(Collections.singletonList(repository)));
        when(libraryService.createUpdateLibraries(any()))
            .thenReturn(Collections.singletonList(library));

        gitService.updateWidgetFromEnabledGitRepositoriesAsync();

        verify(repositoryService)
            .findAllByEnabledOrderByPriorityDescCreatedDateAsc(true);
        verify(libraryService).createUpdateLibraries(argThat(libraries ->
            libraries.get(0).getTechnicalName().equals("test.js")
                && libraries.get(0).getAsset() != null
                && libraries.get(0).getAsset().getContentType().equals("application/javascript")));
        verify(categoryService)
            .addOrUpdateCategory(argThat(category -> category.getName().equals("GitHub")
                && category.getTechnicalName().equals("github")));
        verify(categoryService)
            .addOrUpdateCategory(argThat(category -> category.getName().equals("GitLab")
                && category.getTechnicalName().equals("gitlab")));
        verify(widgetService)
            .addOrUpdateWidgets(argThat(category -> category.getName().equals("GitHub")
                    && category.getTechnicalName().equals("github")),
                argThat(allLibraries -> allLibraries.get(0).equals(library)),
                argThat(repository::equals));
        verify(widgetService)
            .addOrUpdateWidgets(argThat(category -> category.getName().equals("GitLab")
                    && category.getTechnicalName().equals("gitlab")),
                argThat(allLibraries -> allLibraries.get(0).equals(library)),
                argThat(repository::equals));
        verify(cacheService).clearAllCache();
    }

    @Test
    void shouldCatchExceptionWhenUpdateWidgetFromEnabledGitRepositoriesAsync() {
        ApplicationProperties.Widgets widgetsProperties = new ApplicationProperties.Widgets();
        widgetsProperties.setUpdateEnable(true);

        Repository repository = new Repository();
        repository.setId(1L);
        repository.setName("repository");
        repository.setBranch("master");
        repository.setType(RepositoryTypeEnum.LOCAL);
        repository.setLocalPath("unknown");

        when(applicationProperties.getWidgets())
            .thenReturn(widgetsProperties);
        when(repositoryService.findAllByEnabledOrderByPriorityDescCreatedDateAsc(true))
            .thenReturn(Optional.of(Collections.singletonList(repository)));

        gitService.updateWidgetFromEnabledGitRepositoriesAsync();

        verify(jsExecutionScheduler)
            .init();
        verify(dashboardWebSocketService)
            .reloadAllConnectedClientsToAllProjects();
        verify(repositoryService)
            .findAllByEnabledOrderByPriorityDescCreatedDateAsc(true);
    }

    @Test
    void shouldUpdateWidgetFromEnabledGitRepositories() throws GitAPIException, IOException {
        ApplicationProperties.Widgets widgetsProperties = new ApplicationProperties.Widgets();
        widgetsProperties.setUpdateEnable(true);

        Library library = new Library();
        library.setTechnicalName("test.js");

        Repository repository = new Repository();
        repository.setId(1L);
        repository.setName("repository");
        repository.setBranch("master");
        repository.setType(RepositoryTypeEnum.LOCAL);
        repository.setLocalPath("src/test/resources/repository");

        when(applicationProperties.getWidgets())
            .thenReturn(widgetsProperties);
        when(repositoryService.findAllByEnabledOrderByPriorityDescCreatedDateAsc(true))
            .thenReturn(Optional.of(Collections.singletonList(repository)));
        when(libraryService.createUpdateLibraries(any()))
            .thenReturn(Collections.singletonList(library));

        gitService.updateWidgetFromEnabledGitRepositories();

        verify(repositoryService)
            .findAllByEnabledOrderByPriorityDescCreatedDateAsc(true);
        verify(libraryService).createUpdateLibraries(argThat(libraries ->
            libraries.get(0).getTechnicalName().equals("test.js")
                && libraries.get(0).getAsset() != null
                && libraries.get(0).getAsset().getContentType().equals("application/javascript")));
        verify(categoryService)
            .addOrUpdateCategory(argThat(category -> category.getName().equals("GitHub")
                && category.getTechnicalName().equals("github")));
        verify(categoryService)
            .addOrUpdateCategory(argThat(category -> category.getName().equals("GitLab")
                && category.getTechnicalName().equals("gitlab")));
        verify(widgetService)
            .addOrUpdateWidgets(argThat(category -> category.getName().equals("GitHub")
                    && category.getTechnicalName().equals("github")),
                argThat(allLibraries -> allLibraries.get(0).equals(library)),
                argThat(repository::equals));
        verify(widgetService)
            .addOrUpdateWidgets(argThat(category -> category.getName().equals("GitLab")
                    && category.getTechnicalName().equals("gitlab")),
                argThat(allLibraries -> allLibraries.get(0).equals(library)),
                argThat(repository::equals));
        verify(cacheService).clearAllCache();
    }

    @Test
    void shouldNotUpdateWidgetFromEnabledGitRepositoriesWhenUpdateIsDisabled() throws GitAPIException, IOException {
        when(applicationProperties.getWidgets())
            .thenReturn(new ApplicationProperties.Widgets());

        gitService.updateWidgetFromEnabledGitRepositories();

        verify(repositoryService, times(0))
            .findAllByEnabledOrderByPriorityDescCreatedDateAsc(true);
    }

    @Test
    void shouldNotUpdateWidgetFromEnabledGitRepositoriesWhenNoRepository() throws GitAPIException, IOException {
        ApplicationProperties.Widgets widgetsProperties = new ApplicationProperties.Widgets();
        widgetsProperties.setUpdateEnable(true);

        when(applicationProperties.getWidgets())
            .thenReturn(widgetsProperties);
        when(repositoryService.findAllByEnabledOrderByPriorityDescCreatedDateAsc(true))
            .thenReturn(Optional.empty());

        gitService.updateWidgetFromEnabledGitRepositories();

        verify(repositoryService)
            .findAllByEnabledOrderByPriorityDescCreatedDateAsc(true);
    }

    @Test
    void shouldReadWidgetLocalRepository() throws GitAPIException, IOException {
        Library library = new Library();
        library.setTechnicalName("test.js");

        Repository repository = new Repository();
        repository.setId(1L);
        repository.setName("repository");
        repository.setBranch("master");
        repository.setType(RepositoryTypeEnum.LOCAL);
        repository.setLocalPath("src/test/resources/repository");

        when(libraryService.createUpdateLibraries(any()))
            .thenReturn(Collections.singletonList(library));

        gitService.readWidgetRepositories(Collections.singletonList(repository));

        verify(libraryService).createUpdateLibraries(argThat(libraries ->
            libraries.get(0).getTechnicalName().equals("test.js")
                && libraries.get(0).getAsset() != null
                && libraries.get(0).getAsset().getContentType().equals("application/javascript")));
        verify(categoryService)
            .addOrUpdateCategory(argThat(category -> category.getName().equals("GitHub")
                && category.getTechnicalName().equals("github")));
        verify(categoryService)
            .addOrUpdateCategory(argThat(category -> category.getName().equals("GitLab")
                && category.getTechnicalName().equals("gitlab")));
        verify(widgetService)
            .addOrUpdateWidgets(argThat(category -> category.getName().equals("GitHub")
                    && category.getTechnicalName().equals("github")),
                argThat(allLibraries -> allLibraries.get(0).equals(library)),
                argThat(repository::equals));
        verify(widgetService)
            .addOrUpdateWidgets(argThat(category -> category.getName().equals("GitLab")
                    && category.getTechnicalName().equals("gitlab")),
                argThat(allLibraries -> allLibraries.get(0).equals(library)),
                argThat(repository::equals));
        verify(cacheService).clearAllCache();
    }

    @Test
    void shouldThrowExceptionWhenReadWidgetLocalRepository() {
        Repository repository = new Repository();
        repository.setId(1L);
        repository.setName("repository");
        repository.setBranch("master");
        repository.setType(RepositoryTypeEnum.LOCAL);
        repository.setLocalPath("unknown");

        assertThrows(
            IOException.class,
            () -> gitService.readWidgetRepositories(Collections.singletonList(repository))
        );

        verify(jsExecutionScheduler)
            .init();
        verify(dashboardWebSocketService)
            .reloadAllConnectedClientsToAllProjects();
    }

    @Test
    void shouldReadWidgetRemoteRepository() throws GitAPIException, IOException {
        ApplicationProperties.Widgets widgetsProperties = new ApplicationProperties.Widgets();
        widgetsProperties.setCloneDir("/tmp");

        Library library = new Library();
        library.setTechnicalName("test.js");

        Repository repository = new Repository();
        repository.setId(1L);
        repository.setName("repository");
        repository.setUrl("https://github.com/michelin/suricate-widgets");
        repository.setBranch("master");
        repository.setType(RepositoryTypeEnum.REMOTE);

        when(libraryService.createUpdateLibraries(any()))
            .thenReturn(Collections.singletonList(library));
        when(applicationProperties.getWidgets())
            .thenReturn(widgetsProperties);

        gitService.readWidgetRepositories(Collections.singletonList(repository));

        verify(libraryService)
            .createUpdateLibraries(anyList());
        verify(categoryService, atLeastOnce())
            .addOrUpdateCategory(any());
        verify(widgetService, atLeastOnce())
            .addOrUpdateWidgets(any(), any(), any());
        verify(cacheService)
            .clearAllCache();
    }

    @Test
    void shouldThrowExceptionWhenReadWidgetRemoteRepository() {
        ApplicationProperties.Widgets widgetsProperties = new ApplicationProperties.Widgets();
        widgetsProperties.setCloneDir("/tmp");

        Library library = new Library();
        library.setTechnicalName("test.js");

        Repository repository = new Repository();
        repository.setId(1L);
        repository.setName("repository");
        repository.setUrl("http://url");
        repository.setBranch("master");
        repository.setLogin("login");
        repository.setPassword("password");
        repository.setType(RepositoryTypeEnum.REMOTE);

        when(applicationProperties.getWidgets())
            .thenReturn(widgetsProperties);

        Exception exception = assertThrows(
            Exception.class,
            () -> gitService.readWidgetRepositories(Collections.singletonList(repository))
        );

        assertEquals("Exception caught during execution of fetch command", exception.getMessage());

        verify(jsExecutionScheduler)
            .init();
        verify(dashboardWebSocketService)
            .reloadAllConnectedClientsToAllProjects();
    }
}
