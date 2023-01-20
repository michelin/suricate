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

import io.suricate.monitoring.model.entities.Category;
import io.suricate.monitoring.model.entities.Library;
import io.suricate.monitoring.model.entities.Repository;
import io.suricate.monitoring.model.enums.RepositoryTypeEnum;
import io.suricate.monitoring.properties.ApplicationProperties;
import io.suricate.monitoring.services.api.CategoryService;
import io.suricate.monitoring.services.api.LibraryService;
import io.suricate.monitoring.services.api.RepositoryService;
import io.suricate.monitoring.services.api.WidgetService;
import io.suricate.monitoring.services.cache.CacheService;
import io.suricate.monitoring.services.nashorn.scheduler.NashornRequestWidgetExecutionScheduler;
import io.suricate.monitoring.services.websocket.DashboardWebSocketService;
import io.suricate.monitoring.utils.WidgetUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GitService {
    @Autowired
    private NashornRequestWidgetExecutionScheduler nashornWidgetScheduler;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private WidgetService widgetService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private LibraryService libraryService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private DashboardWebSocketService dashboardWebSocketService;

    @Autowired
    private CacheService cacheService;

    /**
     * Update widgets from the full list of git repositories asynchronously
     */
    @Async
    @Transactional
    public void updateWidgetFromEnabledGitRepositoriesAsync() {
        try {
            updateWidgetFromEnabledGitRepositories();
        } catch (Exception e) {
            log.error("An error has occurred when cloning and updating the widgets from the repositories", e);
        }
    }

    /**
     * Update widgets from the full list of git repositories
     */
    @Transactional
    public void updateWidgetFromEnabledGitRepositories() throws GitAPIException, IOException {
        log.info("Update widgets from Git repository");

        if (!applicationProperties.widgets.updateEnable) {
            log.info("Widget update disabled");
            return;
        }

        Optional<List<Repository>> optionalRepositories = repositoryService.findAllByEnabledOrderByPriorityDescCreatedDateAsc(true);
        if (!optionalRepositories.isPresent()) {
            log.info("No remote or local repository found");
            return;
        }

        readWidgetRepositories(optionalRepositories.get());
    }

    /**
     * Clone and update the widgets from the given list of repositories
     */
    @Transactional
    public void readWidgetRepositories(final List<Repository> repositories) throws GitAPIException, IOException {
        try {
            for (Repository repository : repositories) {
                if (repository.getType() == RepositoryTypeEnum.LOCAL) {
                    log.info("Loading widgets from the local folder {}", repository.getLocalPath());

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
        log.info("Cloning the branch {} of the remote repository {}", branch, url);

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
            log.info("The branch {} from the remote repository {} was successfully cloned", git.getRepository().getBranch(), url);
        } catch (Exception e) {
            log.error("An error has occurred while trying to clone the branch {} of the remote repository {}", branch, url, e);
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
