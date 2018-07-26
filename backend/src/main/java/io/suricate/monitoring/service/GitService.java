/*
 * Copyright 2012-2018 the original author or authors.
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

package io.suricate.monitoring.service;

import io.suricate.monitoring.configuration.ApplicationProperties;
import io.suricate.monitoring.model.entity.Library;
import io.suricate.monitoring.model.entity.widget.Category;
import io.suricate.monitoring.service.api.LibraryService;
import io.suricate.monitoring.service.api.RepositoryService;
import io.suricate.monitoring.service.api.WidgetService;
import io.suricate.monitoring.service.scheduler.NashornWidgetScheduler;
import io.suricate.monitoring.service.webSocket.DashboardWebSocketService;
import io.suricate.monitoring.utils.WidgetUtils;
import io.suricate.monitoring.utils.exception.ConfigurationException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.jgit.api.Git;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
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
     * The application properties
     */
    private final ApplicationProperties applicationProperties;

    /**
     * The widget service
     */
    private final WidgetService widgetService;

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
     * The nashorn widget executor
     */
    private final NashornWidgetScheduler nashornWidgetScheduler;

    /**
     * Contructor using fields
     *
     * @param widgetService             widget service
     * @param libraryService            library service
     * @param repositoryService         The repository service
     * @param dashboardWebSocketService socket service
     * @param nashornWidgetScheduler    widget executor
     * @param applicationProperties     The application properties
     */
    @Autowired
    public GitService(final WidgetService widgetService,
                      final LibraryService libraryService,
                      final RepositoryService repositoryService,
                      final DashboardWebSocketService dashboardWebSocketService,
                      final NashornWidgetScheduler nashornWidgetScheduler,
                      final ApplicationProperties applicationProperties) {
        this.widgetService = widgetService;
        this.libraryService = libraryService;
        this.repositoryService = repositoryService;
        this.dashboardWebSocketService = dashboardWebSocketService;
        this.nashornWidgetScheduler = nashornWidgetScheduler;
        this.applicationProperties = applicationProperties;
    }

    /**
     * Methods used to clone widget repo
     *
     * @return the folder containing the widget repo
     */
    public File cloneWidgetRepo() throws Exception {
        if (StringUtils.isNotBlank(applicationProperties.widgets.local.folderPath)) {
            LOGGER.info("Loading widget from local folder {}", applicationProperties.widgets.local.folderPath);
            return new File(applicationProperties.widgets.local.folderPath);
        }

        if (StringUtils.isBlank(applicationProperties.widgets.git.url)) {
            throw new ConfigurationException("A git url is mandatory when no widget local folder is set", "application.widgets.git.url");
        }
        if (StringUtils.isBlank(applicationProperties.widgets.git.branch)) {
            throw new ConfigurationException("A git branch is mandatory when no widget local folder is set", "application.widgets.git.branch");
        }
        return cloneRepo(applicationProperties.widgets.git.url, applicationProperties.widgets.git.branch);
    }

    /**
     * Clone a remote repository on local system.
     *
     * @param url    git repository url
     * @param branch git branch
     * @return File object on local repo
     */
    public File cloneRepo(String url, String branch) throws Exception {
        LOGGER.info("Cloning widget repo {}, branch {}", url, branch);
        File localRepo = null;
        Git git = null;
        try {
            localRepo = File.createTempFile("tmp", Long.toString(System.nanoTime()));
            if (localRepo.exists()) {
                FileUtils.deleteQuietly(localRepo);
            }
            localRepo.mkdirs();

            String remoteRepo = new URL(url).toExternalForm();
            git = Git.cloneRepository()
                .setURI(remoteRepo)
                .setBranch(branch)
                .setDirectory(localRepo)
                .call();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            FileUtils.deleteQuietly(localRepo);
            throw e;
        } finally {
            if (git != null) {
                git.getRepository().close();
            }
        }

        return localRepo;
    }

    /**
     * Async method used to update widget from git
     *
     * @return True as Future when the process has been done
     */
    @Async
    @Transactional
    public Future<Boolean> updateWidgetFromGitRepositories() {
        LOGGER.info("Update widgets from Git repo");
        if (!applicationProperties.widgets.updateEnable) {
            LOGGER.info("Widget update disabled");
            return null;
        }


        File folder = null;
        try {
            folder = cloneWidgetRepo();
            if (folder != null) {
                // Libraries
                File libraryFolder = new File(folder.getAbsoluteFile().getAbsolutePath() + SystemUtils.FILE_SEPARATOR + "libraries" + SystemUtils.FILE_SEPARATOR);
                List<Library> libraries = WidgetUtils.parseLibraryFolder(libraryFolder);
                libraries = libraryService.updateLibraryInDatabase(libraries);
                Map<String, Library> mapLib = libraries.stream().collect(Collectors.toMap(item -> ((Library) item).getTechnicalName(), item -> item));

                // Parse folder
                File widgetFolder = new File(folder.getAbsoluteFile().getAbsolutePath() + SystemUtils.FILE_SEPARATOR + "content" + SystemUtils.FILE_SEPARATOR);
                List<Category> list = WidgetUtils.parseWidgetFolder(widgetFolder);
                widgetService.updateWidgetInDatabase(list, mapLib);

                return new AsyncResult<>(true);
            }
        } catch (Exception ioe) {
            LOGGER.error(ioe.getMessage(), ioe);
        } finally {
            if (StringUtils.isBlank(applicationProperties.widgets.local.folderPath)) {
                FileUtils.deleteQuietly(folder);
            }
            nashornWidgetScheduler.initScheduler();
            dashboardWebSocketService.reloadAllConnectedDashboard();
        }

        return new AsyncResult<>(false);
    }
}
