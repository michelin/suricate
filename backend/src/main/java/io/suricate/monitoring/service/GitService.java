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
import io.suricate.monitoring.model.entity.widget.Category;
import io.suricate.monitoring.model.entity.Library;
import io.suricate.monitoring.utils.WidgetUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.eclipse.jgit.api.Git;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GitService {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GitService.class);

    /** The application properties */
    private final ApplicationProperties applicationProperties;

    private final WidgetService widgetService;

    private final LibraryService libraryService;

    private final SocketService socketService;

    private final WidgetExecutor widgetExecutor;

    /**
     * Contructor using fields
     * @param widgetService widget service
     * @param libraryService library service
     * @param socketService socket service
     * @param widgetExecutor widget executor
     */
    @Autowired
    public GitService(WidgetService widgetService, LibraryService libraryService, SocketService socketService, WidgetExecutor widgetExecutor, ApplicationProperties applicationProperties) {
        this.widgetService = widgetService;
        this.libraryService = libraryService;
        this.socketService = socketService;
        this.widgetExecutor = widgetExecutor;
        this.applicationProperties = applicationProperties;
    }

    /**
     * Methods used to clone widget repo
     * @return the folder containing the widget repo
     * @throws IOException
     */
    public File cloneWidgetRepo() throws Exception {
        if (StringUtils.isNotBlank(applicationProperties.widgets.local.folderPath)) {
            LOGGER.info("Loading widget from local folder {}", applicationProperties.widgets.local.folderPath);
            return new File(applicationProperties.widgets.local.folderPath);
        }

        return cloneRepo(applicationProperties.widgets.git.url, applicationProperties.widgets.git.branch);
    }

    /**
     * Clone a remote repository on local system.
     * @param url git repository url
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
            LOGGER.error(e.getMessage(),e);
            FileUtils.deleteQuietly(localRepo);
            throw e;
        } finally {
            if (git != null){
                git.getRepository().close();
            }
        }

        return localRepo;
    }

    /**
     * Async method used to update widget from git
     */
    @Async
    @Transactional
    public void updateWidgetFromGit(){
        LOGGER.info("Update widgets from Git repo");
        if (!applicationProperties.widgets.updateEnable){
            LOGGER.info("Widget update disabled");
            return;
        }
        File folder = null;
        try {
            folder = cloneWidgetRepo();
            if (folder != null) {
                // Libraries
                File libraryFolder = new File(folder.getAbsoluteFile().getAbsolutePath() + SystemUtils.FILE_SEPARATOR + "libraries"+ SystemUtils.FILE_SEPARATOR);
                List<Library> libraries = WidgetUtils.parseLibraryFolder(libraryFolder);
                libraries = libraryService.updateLibraryInDatabase(libraries);
                Map<String, Library> mapLib = libraries.stream().collect(Collectors.toMap(item -> ((Library)item).getTechnicalName(), item -> item));

                // Parse folder
                File widgetFolder = new File(folder.getAbsoluteFile().getAbsolutePath() + SystemUtils.FILE_SEPARATOR + "content"+ SystemUtils.FILE_SEPARATOR);
                List<Category> list = WidgetUtils.parseWidgetFolder(widgetFolder);
                widgetService.updateWidgetInDatabase(list, mapLib);
            }
        } catch (Exception ioe) {
            LOGGER.error(ioe.getMessage(), ioe);
        } finally {
            if (StringUtils.isBlank(applicationProperties.widgets.local.folderPath)) {
                FileUtils.deleteQuietly(folder);
            }
            widgetExecutor.initScheduler();
            socketService.reloadAllConnectedDashboard();
        }
    }
}
