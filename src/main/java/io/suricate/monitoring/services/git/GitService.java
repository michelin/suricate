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

package io.suricate.monitoring.services.git;

import io.suricate.monitoring.properties.ApplicationProperties;
import io.suricate.monitoring.model.entities.Library;
import io.suricate.monitoring.model.entities.Category;
import io.suricate.monitoring.model.entities.Repository;
import io.suricate.monitoring.model.enums.RepositoryTypeEnum;
import io.suricate.monitoring.services.cache.CacheService;
import io.suricate.monitoring.services.api.CategoryService;
import io.suricate.monitoring.services.api.LibraryService;
import io.suricate.monitoring.services.api.RepositoryService;
import io.suricate.monitoring.services.api.WidgetService;
import io.suricate.monitoring.services.nashorn.scheduler.NashornRequestWidgetExecutionScheduler;
import io.suricate.monitoring.services.websocket.DashboardWebSocketService;
import io.suricate.monitoring.utils.WidgetUtils;
import io.suricate.monitoring.utils.exceptions.RepositorySyncException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
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
import java.util.Optional;
import java.util.concurrent.Future;

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
    @Autowired
    private NashornRequestWidgetExecutionScheduler nashornWidgetScheduler;

    /**
     * The application properties
     */
    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * The widget service
     */
    @Autowired
    private WidgetService widgetService;

    /**
     * The widget service
     */
    @Autowired
    private CategoryService categoryService;

    /**
     * The library service
     */
    @Autowired
    private LibraryService libraryService;

    /**
     * The repository service
     */
    @Autowired
    private RepositoryService repositoryService;

    /**
     * The dashboard websocket service
     */
    @Autowired
    private DashboardWebSocketService dashboardWebSocketService;

    /**
     * Cache service
     */
    @Autowired
    private CacheService cacheService;

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

        try {
            readWidgetRepositories(optionalRepositories.get());
            return new AsyncResult<>(true);
        } catch (Exception e) {
            LOGGER.error("An error has occurred when cloning and updating the widgets from the repositories", e);
        }

        return new AsyncResult<>(false);
    }

    /**
     * Update widgets contained in the given repository
     * @param repository The repository
     * @return true if the update worked, false otherwise
     */
    @Transactional
    public boolean updateWidgetsFromRepository(Repository repository) throws GitAPIException, IOException {
        if (repository == null) {
            LOGGER.debug("The repository can't be null");
            return false;
        }

        if (!applicationProperties.widgets.updateEnable) {
            LOGGER.info("Widget update disabled");
            return true;
        } else {
            LOGGER.info("Update widgets from Git repository {}", repository.getName());
        }

        if (!repository.isEnabled()) {
            LOGGER.info("The repository {} is not enabled", repository.getName());
            return true;
        }

        readWidgetRepositories(Collections.singletonList(repository));

        return true;
    }

    /**
     * Clone and update the widgets from the given list of repositories
     */
    @Transactional
    public void readWidgetRepositories(final List<Repository> repositories) throws GitAPIException, IOException {
        try {
            for (Repository repository : repositories) {
                if (repository.getType() == RepositoryTypeEnum.LOCAL) {
                    LOGGER.info("Loading widgets from the local folder {}", repository.getLocalPath());

                    updateWidgetsFromRepositoryFolder(new File(repository.getLocalPath()), true, repository);
                } else {
                    File remoteFolder = cloneRemoteRepository(repository.getUrl(), repository.getBranch(),
                            repository.getLogin(), repository.getPassword());

                    updateWidgetsFromRepositoryFolder(remoteFolder, false, repository);
                }
            }
        } finally {
            nashornWidgetScheduler.init();
            dashboardWebSocketService.reloadAllConnectedClientsToAllProjects();
        }

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
    public File cloneRemoteRepository(String url, String branch, String login, String password) throws IOException, GitAPIException {
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
            throw e;
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
    private void updateWidgetsFromRepositoryFolder(File folder, boolean isLocalRepository, final Repository repository) throws IOException {
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
                    categoryService.addOrUpdateCategory(category);

                    widgetService.addOrUpdateWidgets(category, allLibraries, repository);
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
