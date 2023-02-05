package com.michelin.suricate.services.git;

import com.michelin.suricate.properties.ApplicationProperties;
import com.michelin.suricate.model.entities.Library;
import com.michelin.suricate.model.entities.Repository;
import com.michelin.suricate.model.enums.RepositoryTypeEnum;
import com.michelin.suricate.services.api.CategoryService;
import com.michelin.suricate.services.api.LibraryService;
import com.michelin.suricate.services.api.RepositoryService;
import com.michelin.suricate.services.api.WidgetService;
import com.michelin.suricate.services.cache.CacheService;
import com.michelin.suricate.services.nashorn.scheduler.NashornRequestWidgetExecutionScheduler;
import com.michelin.suricate.services.websocket.DashboardWebSocketService;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GitServiceTest {
    @Mock
    private NashornRequestWidgetExecutionScheduler nashornWidgetScheduler;

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
        doNothing().when(categoryService)
                .addOrUpdateCategory(any());
        doNothing().when(widgetService)
                .addOrUpdateWidgets(any(), any(), any());
        doNothing().when(cacheService)
                .clearAllCache();

        gitService.updateWidgetFromEnabledGitRepositoriesAsync();

        verify(repositoryService, times(1))
                .findAllByEnabledOrderByPriorityDescCreatedDateAsc(true);
        verify(libraryService, times(1)).createUpdateLibraries(argThat(libraries ->
                libraries.get(0).getTechnicalName().equals("test.js") &&
                        Arrays.equals(libraries.get(0).getAsset().getContent(), new byte[]{108, 101, 116, 32, 116, 101, 115, 116, 59}) &&
                        libraries.get(0).getAsset().getSize() == 9 &&
                        libraries.get(0).getAsset().getContentType().equals("application/javascript")));
        verify(categoryService, times(1))
                .addOrUpdateCategory(argThat(category -> category.getName().equals("GitHub") &&
                        category.getTechnicalName().equals("github")));
        verify(categoryService, times(1))
                .addOrUpdateCategory(argThat(category -> category.getName().equals("GitLab") &&
                        category.getTechnicalName().equals("gitlab")));
        verify(widgetService, times(1))
                .addOrUpdateWidgets(argThat(category -> category.getName().equals("GitHub") &&
                                category.getTechnicalName().equals("github")),
                        argThat(allLibraries -> allLibraries.get(0).equals(library)),
                        argThat(repository::equals));
        verify(widgetService, times(1))
                .addOrUpdateWidgets(argThat(category -> category.getName().equals("GitLab") &&
                                category.getTechnicalName().equals("gitlab")),
                        argThat(allLibraries -> allLibraries.get(0).equals(library)),
                        argThat(repository::equals));
        verify(cacheService, times(1)).clearAllCache();
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
        doNothing().when(nashornWidgetScheduler)
                .init();
        doNothing().when(dashboardWebSocketService)
                .reloadAllConnectedClientsToAllProjects();
        when(repositoryService.findAllByEnabledOrderByPriorityDescCreatedDateAsc(true))
                .thenReturn(Optional.of(Collections.singletonList(repository)));

        gitService.updateWidgetFromEnabledGitRepositoriesAsync();

        verify(nashornWidgetScheduler, times(1))
                .init();
        verify(dashboardWebSocketService, times(1))
                .reloadAllConnectedClientsToAllProjects();
        verify(repositoryService, times(1))
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
        doNothing().when(categoryService)
                .addOrUpdateCategory(any());
        doNothing().when(widgetService)
                .addOrUpdateWidgets(any(), any(), any());
        doNothing().when(cacheService)
                .clearAllCache();

        gitService.updateWidgetFromEnabledGitRepositories();

        verify(repositoryService, times(1))
                .findAllByEnabledOrderByPriorityDescCreatedDateAsc(true);
        verify(libraryService, times(1)).createUpdateLibraries(argThat(libraries ->
                libraries.get(0).getTechnicalName().equals("test.js") &&
                        Arrays.equals(libraries.get(0).getAsset().getContent(), new byte[]{108, 101, 116, 32, 116, 101, 115, 116, 59}) &&
                        libraries.get(0).getAsset().getSize() == 9 &&
                        libraries.get(0).getAsset().getContentType().equals("application/javascript")));
        verify(categoryService, times(1))
                .addOrUpdateCategory(argThat(category -> category.getName().equals("GitHub") &&
                        category.getTechnicalName().equals("github")));
        verify(categoryService, times(1))
                .addOrUpdateCategory(argThat(category -> category.getName().equals("GitLab") &&
                        category.getTechnicalName().equals("gitlab")));
        verify(widgetService, times(1))
                .addOrUpdateWidgets(argThat(category -> category.getName().equals("GitHub") &&
                                category.getTechnicalName().equals("github")),
                        argThat(allLibraries -> allLibraries.get(0).equals(library)),
                        argThat(repository::equals));
        verify(widgetService, times(1))
                .addOrUpdateWidgets(argThat(category -> category.getName().equals("GitLab") &&
                                category.getTechnicalName().equals("gitlab")),
                        argThat(allLibraries -> allLibraries.get(0).equals(library)),
                        argThat(repository::equals));
        verify(cacheService, times(1)).clearAllCache();
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

        verify(repositoryService, times(1))
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
        doNothing().when(categoryService)
                .addOrUpdateCategory(any());
        doNothing().when(widgetService)
                .addOrUpdateWidgets(any(), any(), any());
        doNothing().when(cacheService)
                .clearAllCache();

        gitService.readWidgetRepositories(Collections.singletonList(repository));

        verify(libraryService, times(1)).createUpdateLibraries(argThat(libraries ->
                libraries.get(0).getTechnicalName().equals("test.js") &&
                Arrays.equals(libraries.get(0).getAsset().getContent(), new byte[]{108, 101, 116, 32, 116, 101, 115, 116, 59}) &&
                libraries.get(0).getAsset().getSize() == 9 &&
                libraries.get(0).getAsset().getContentType().equals("application/javascript")));
        verify(categoryService, times(1))
                .addOrUpdateCategory(argThat(category -> category.getName().equals("GitHub") &&
                        category.getTechnicalName().equals("github")));
        verify(categoryService, times(1))
                .addOrUpdateCategory(argThat(category -> category.getName().equals("GitLab") &&
                        category.getTechnicalName().equals("gitlab")));
        verify(widgetService, times(1))
                .addOrUpdateWidgets(argThat(category -> category.getName().equals("GitHub") &&
                        category.getTechnicalName().equals("github")),
                        argThat(allLibraries -> allLibraries.get(0).equals(library)),
                        argThat(repository::equals));
        verify(widgetService, times(1))
                .addOrUpdateWidgets(argThat(category -> category.getName().equals("GitLab") &&
                                category.getTechnicalName().equals("gitlab")),
                        argThat(allLibraries -> allLibraries.get(0).equals(library)),
                        argThat(repository::equals));
        verify(cacheService, times(1)).clearAllCache();
    }

    @Test
    void shouldThrowExceptionWhenReadWidgetLocalRepository() {
        Repository repository = new Repository();
        repository.setId(1L);
        repository.setName("repository");
        repository.setBranch("master");
        repository.setType(RepositoryTypeEnum.LOCAL);
        repository.setLocalPath("unknown");

        doNothing().when(nashornWidgetScheduler)
                .init();
        doNothing().when(dashboardWebSocketService)
                .reloadAllConnectedClientsToAllProjects();

        assertThatThrownBy(() -> gitService.readWidgetRepositories(Collections.singletonList(repository)))
                .isInstanceOf(IOException.class);

        verify(nashornWidgetScheduler, times(1))
                .init();
        verify(dashboardWebSocketService, times(1))
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
        doNothing().when(categoryService)
                .addOrUpdateCategory(any());
        doNothing().when(widgetService)
                .addOrUpdateWidgets(any(), any(), any());
        doNothing().when(cacheService)
                .clearAllCache();

        gitService.readWidgetRepositories(Collections.singletonList(repository));

        verify(libraryService, times(1))
                .createUpdateLibraries(anyList());
        verify(categoryService, atLeastOnce())
                .addOrUpdateCategory(any());
        verify(widgetService, atLeastOnce())
                .addOrUpdateWidgets(any(), any(), any());
        verify(cacheService, times(1))
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
        doNothing().when(nashornWidgetScheduler)
                .init();
        doNothing().when(dashboardWebSocketService)
                .reloadAllConnectedClientsToAllProjects();

        assertThatThrownBy(() -> gitService.readWidgetRepositories(Collections.singletonList(repository)))
                .isInstanceOf(Exception.class)
                .hasMessage("Exception caught during execution of fetch command");

        verify(nashornWidgetScheduler, times(1))
                .init();
        verify(dashboardWebSocketService, times(1))
                .reloadAllConnectedClientsToAllProjects();
    }
}
