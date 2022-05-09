/*
 * Copyright 2012-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.suricate.monitoring.services;

import io.suricate.monitoring.configuration.ApplicationProperties;
import io.suricate.monitoring.model.entities.Library;
import io.suricate.monitoring.model.entities.Category;
import io.suricate.monitoring.model.entities.Repository;
import io.suricate.monitoring.model.enums.RepositoryTypeEnum;
import io.suricate.monitoring.services.api.CategoryService;
import io.suricate.monitoring.services.api.LibraryService;
import io.suricate.monitoring.services.api.RepositoryService;
import io.suricate.monitoring.services.api.WidgetService;
import io.suricate.monitoring.services.nashorn.scheduler.NashornRequestWidgetExecutionScheduler;
import io.suricate.monitoring.services.websocket.DashboardWebSocketService;
import io.suricate.monitoring.utils.WidgetUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Manage git calls
 */
@Service
public class GitService {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GitService.class);

    /**
     * The scheduler scheduling the widget execution through Nashorn
     */
    private final NashornRequestWidgetExecutionScheduler nashornWidgetScheduler;

    /**
     * The application properties
     */
    private final ApplicationProperties applicationProperties;

    /**
     * The widget service
     */
    private final WidgetService widgetService;

    /**
     * The widget service
     */
    private final CategoryService categoryService;

    /**
     * The library service
     */
    private final LibraryService libraryService;

    /**
     * The repository service
     */
    private final RepositoryService repositoryService;

    /**
     * The dashboard websocket service
     */
    private final DashboardWebSocketService dashboardWebSocketService;

    /**
     * Cache service
     */
    private final CacheService cacheService;

    /**
     * Constructor
     *
     * @param widgetService             Widget service
     * @param categoryService           Category service
     * @param libraryService            Library service
     * @param cacheService              Cache service
     * @param repositoryService         Repository service
     * @param dashboardWebSocketService Web socket service
     * @param nashornWidgetScheduler    Nashorn scheduler
     * @param applicationProperties     Application properties
     */
    @Autowired
    public GitService(final WidgetService widgetService,
                      final CategoryService categoryService,
                      final CacheService cacheService,
                      final LibraryService libraryService,
                      final RepositoryService repositoryService,
                      final DashboardWebSocketService dashboardWebSocketService,
                      final NashornRequestWidgetExecutionScheduler nashornWidgetScheduler,
                      final ApplicationProperties applicationProperties) {
        this.widgetService = widgetService;
        this.categoryService = categoryService;
        this.libraryService = libraryService;
        this.cacheService = cacheService;
        this.repositoryService = repositoryService;
        this.dashboardWebSocketService = dashboardWebSocketService;
        this.nashornWidgetScheduler = nashornWidgetScheduler;
        this.applicationProperties = applicationProperties;
    }


    /**
     * Async method used to update widgets from the full list of git repositories
     *
     * @return True as Future when the process has been done
     */
    @Async
    @Transactional
    public Future<Boolean> updateWidgetFromEnabledGitRepositories() {
        LOGGER.info("Update widgets from Git repository");

        if (!applicationProperties.widgets.updateEnable) {
            LOGGER.info("Widget update disabled");
            return null;
        }

        Optional<List<Repository>> optionalRepositories = repositoryService.getAllByEnabledOrderByName(true);
        if (!optionalRepositories.isPresent()) {
            LOGGER.info("No remote or local repository found");
            return new AsyncResult<>(true);
        }

        return new AsyncResult<>(readWidgetRepositories(optionalRepositories.get()));
    }

    /**
     * Update widgets contained in the given repository
     *
     * @param repository The repository
     * @return True if the update has been done correctly, false otherwise
     */
    @Async
    @Transactional
    public Future<Boolean> updateWidgetsFromRepository(Repository repository) {
        if (repository == null) {
            LOGGER.debug("The repository can't be null");
            return new AsyncResult<>(false);
        }

        LOGGER.info("Update widgets from Git repository {}", repository.getName());
        if (!applicationProperties.widgets.updateEnable) {
            LOGGER.info("Widget update disabled");
            return null;
        }

        if (!repository.isEnabled()) {
            LOGGER.info("The repository {} is not enabled", repository.getName());
            return null;
        }

        return new AsyncResult<>(this.readWidgetRepositories(Collections.singletonList(repository)));
    }

    /**
     * Clone and update the widgets from the given list of repositories
     *
     * @return true if the widgets update has been done properly
     */
    @Transactional
    public boolean readWidgetRepositories(final List<Repository> repositories) {
        try {
            for (Repository repository : repositories) {
                if (repository.getType() == RepositoryTypeEnum.LOCAL) {
                    LOGGER.info("Loading widgets from the local folder {}", repository.getLocalPath());

                    this.updateWidgetsFromRepositoryFolder(new File(repository.getLocalPath()), true, repository);
                } else {
                    File remoteFolder = this.cloneRemoteRepository(repository.getUrl(), repository.getBranch(),
                            repository.getLogin(), repository.getPassword());

                    this.updateWidgetsFromRepositoryFolder(remoteFolder, false, repository);
                }
            }

            return true;
        } catch (Exception exception) {
            LOGGER.error("An error has occurred when cloning and updating the widgets from the repositories", exception);
        } finally {
            nashornWidgetScheduler.init();
            dashboardWebSocketService.reloadAllConnectedClientsToAllProjects();
        }

        return false;
    }

    /**
     * Clone a remote repository on local system.
     *
     * @param url      git repository url
     * @param branch   git branch
     * @param login    The login of the git repo
     * @param password The password of the git repo
     * @return File object on local repo
     */
    public File cloneRemoteRepository(String url, String branch, String login, String password) throws IOException {
        LOGGER.info("Cloning the branch {} of the remote repository {}", branch, url);

        File localRepository = File.createTempFile("tmp", Long.toString(System.nanoTime()));

        if (localRepository.exists()) {
            FileUtils.deleteQuietly(localRepository);
        }

        localRepository.mkdirs();

        String remoteRepository = new URL(url).toExternalForm();
        CloneCommand cloneCmd = Git.cloneRepository()
            .setURI(remoteRepository)
            .setBranch(branch)
            .setDirectory(localRepository);

        if (StringUtils.isNoneBlank(login, password)) {
            cloneCmd.setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, password));
        }

        try (Git git = cloneCmd.call()) {
            LOGGER.info("The branch {} from the remote repository {} was successfully cloned", git.getRepository().getBranch(), url);
        } catch (Exception e) {
            LOGGER.error("An error has occurred while trying to clone the branch {} of the remote repository {}", branch, url, e);
            FileUtils.deleteQuietly(localRepository);
        }

        return localRepository;
    }

    /**
     * Update the widget in database from cloned folder
     *
     * @param folder            The folder to process
     * @param isLocalRepository True if the folder come from local repository, false if it's a remote repo
     * @param repository        The repository
     */
    private void updateWidgetsFromRepositoryFolder(File folder, boolean isLocalRepository, final Repository repository) {
        if (folder != null) {
            try {
                List<Library> libraries = WidgetUtils
                        .parseLibraryFolder(new File(folder.getAbsoluteFile().getAbsolutePath()
                                + File.separator
                                + "libraries"
                                + File.separator));

                final List<Library> allLibraries = libraryService.createUpdateLibraries(libraries);

                List<Category> categories = WidgetUtils
                        .parseCategoriesFolder(new File(folder.getAbsoluteFile().getAbsolutePath()
                                + File.separator
                                + "content"
                                + File.separator));

                categories.forEach(category -> {
                    this.categoryService.addOrUpdateCategory(category);

                    this.widgetService.addOrUpdateWidgets(category, allLibraries, repository);
                });

                cacheService.clearAllCache();
            } finally {
                if (!isLocalRepository) {
                    FileUtils.deleteQuietly(folder);
                }
            }
        }
    }

}
