/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.michelin.suricate.service.git;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
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

        when(applicationProperties.getWidgets()).thenReturn(widgetsProperties);
        when(repositoryService.findAllByEnabledOrderByPriorityDescCreatedDateAsc(true))
                .thenReturn(Optional.of(Collections.singletonList(repository)));
        when(libraryService.createUpdateLibraries(any())).thenReturn(Collections.singletonList(library));

        gitService.updateWidgetFromEnabledGitRepositoriesAsync();

        verify(repositoryService).findAllByEnabledOrderByPriorityDescCreatedDateAsc(true);
        verify(libraryService)
                .createUpdateLibraries(
                        argThat(libraries -> libraries.get(0).getTechnicalName().equals("test.js")
                                && libraries.get(0).getAsset() != null
                                && libraries.get(0).getAsset().getContentType().equals("application/javascript")));
        verify(categoryService)
                .addOrUpdateCategory(argThat(category -> category.getName().equals("GitHub")
                        && category.getTechnicalName().equals("github")));
        verify(categoryService)
                .addOrUpdateCategory(argThat(category -> category.getName().equals("GitLab")
                        && category.getTechnicalName().equals("gitlab")));
        verify(widgetService)
                .addOrUpdateWidgets(
                        argThat(category -> category.getName().equals("GitHub")
                                && category.getTechnicalName().equals("github")),
                        argThat(allLibraries -> allLibraries.get(0).equals(library)),
                        argThat(repository::equals));
        verify(widgetService)
                .addOrUpdateWidgets(
                        argThat(category -> category.getName().equals("GitLab")
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

        when(applicationProperties.getWidgets()).thenReturn(widgetsProperties);
        when(repositoryService.findAllByEnabledOrderByPriorityDescCreatedDateAsc(true))
                .thenReturn(Optional.of(Collections.singletonList(repository)));

        gitService.updateWidgetFromEnabledGitRepositoriesAsync();

        verify(jsExecutionScheduler).init();
        verify(dashboardWebSocketService).reloadAllConnectedClientsToAllProjects();
        verify(repositoryService).findAllByEnabledOrderByPriorityDescCreatedDateAsc(true);
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

        when(applicationProperties.getWidgets()).thenReturn(widgetsProperties);
        when(repositoryService.findAllByEnabledOrderByPriorityDescCreatedDateAsc(true))
                .thenReturn(Optional.of(Collections.singletonList(repository)));
        when(libraryService.createUpdateLibraries(any())).thenReturn(Collections.singletonList(library));

        gitService.updateWidgetFromEnabledGitRepositories();

        verify(repositoryService).findAllByEnabledOrderByPriorityDescCreatedDateAsc(true);
        verify(libraryService)
                .createUpdateLibraries(
                        argThat(libraries -> libraries.get(0).getTechnicalName().equals("test.js")
                                && libraries.get(0).getAsset() != null
                                && libraries.get(0).getAsset().getContentType().equals("application/javascript")));
        verify(categoryService)
                .addOrUpdateCategory(argThat(category -> category.getName().equals("GitHub")
                        && category.getTechnicalName().equals("github")));
        verify(categoryService)
                .addOrUpdateCategory(argThat(category -> category.getName().equals("GitLab")
                        && category.getTechnicalName().equals("gitlab")));
        verify(widgetService)
                .addOrUpdateWidgets(
                        argThat(category -> category.getName().equals("GitHub")
                                && category.getTechnicalName().equals("github")),
                        argThat(allLibraries -> allLibraries.get(0).equals(library)),
                        argThat(repository::equals));
        verify(widgetService)
                .addOrUpdateWidgets(
                        argThat(category -> category.getName().equals("GitLab")
                                && category.getTechnicalName().equals("gitlab")),
                        argThat(allLibraries -> allLibraries.get(0).equals(library)),
                        argThat(repository::equals));
        verify(cacheService).clearAllCache();
    }

    @Test
    void shouldNotUpdateWidgetFromEnabledGitRepositoriesWhenUpdateIsDisabled() throws GitAPIException, IOException {
        when(applicationProperties.getWidgets()).thenReturn(new ApplicationProperties.Widgets());

        gitService.updateWidgetFromEnabledGitRepositories();

        verify(repositoryService, never()).findAllByEnabledOrderByPriorityDescCreatedDateAsc(true);
    }

    @Test
    void shouldNotUpdateWidgetFromEnabledGitRepositoriesWhenNoRepository() throws GitAPIException, IOException {
        ApplicationProperties.Widgets widgetsProperties = new ApplicationProperties.Widgets();
        widgetsProperties.setUpdateEnable(true);

        when(applicationProperties.getWidgets()).thenReturn(widgetsProperties);
        when(repositoryService.findAllByEnabledOrderByPriorityDescCreatedDateAsc(true))
                .thenReturn(Optional.empty());

        gitService.updateWidgetFromEnabledGitRepositories();

        verify(repositoryService).findAllByEnabledOrderByPriorityDescCreatedDateAsc(true);
    }
}
