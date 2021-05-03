package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.entities.*;
import io.suricate.monitoring.model.enums.DataTypeEnum;
import io.suricate.monitoring.model.enums.RepositoryTypeEnum;
import io.suricate.monitoring.model.enums.WidgetAvailabilityEnum;
import io.suricate.monitoring.model.enums.WidgetStateEnum;
import io.suricate.monitoring.repositories.*;
import io.suricate.monitoring.utils.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

/**
 * Widget service test class
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class WidgetServiceTest {

    /**
     * Mocked css content
     */
    private static final String CSS_CONTENT = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAC2xv8BcPMnUbTx/LEAAAAASUVORK5CYII=) no-repeat left bottom;background-size: contain;opacity: 0.2;height: 25%;width: 25%;position: absolute;left: 5%;bottom: 5%;}.widget.githubOpenedIssues .issues-label {color: #1B1F23;font-size: 40px;}";

    /**
     * Mocked backend JS content
     */
    private static final String BACKEND_JS = "/** Copyright 2012-2018 the original author or authors.** Licensed under the Apache License, Version 2.0 (the \"License\");* you may not use this file except in compliance with the License.* You may obtain a copy of the License at**      http://www.apache.org/licenses/LICENSE-2.0** Unless required by applicable law or agreed to in writing, software* distributed under the License is distributed on an \"AS IS\" BASIS,* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.* See the License for the specific language governing permissions and* limitations under the License.*/function run() {var data = {};var perPage = 100;var issues = [];var page = 1;var response = JSON.parse(Packages.get(\"https://api.github.com/repos/\" + SURI_GITHUB_ORG + \"/\" + SURI_GITHUB_PROJECT + \"/issues?page=\" + page + \"&per_page=\" + perPage + \"&state=\" + SURI_ISSUES_STATE,\"Authorization\", \"token \" + WIDGET_CONFIG_GITHUB_TOKEN));issues = issues.concat(response);while (response && response.length > 0 && response.length === perPage) {page++;response = JSON.parse(Packages.get(\"https://api.github.com/repos/\" + SURI_GITHUB_ORG + \"/\" + SURI_GITHUB_PROJECT + \"/issues?page=\" + page + \"&per_page=\" + perPage + \"&state=\" + SURI_ISSUES_STATE,\"Authorization\", \"token \" + WIDGET_CONFIG_GITHUB_TOKEN));issues = issues.concat(response);}// The response contains the issues and the pull requests. Here, we only keep the real issuesissues = issues.filter(function(issue) {if (!issue.pull_request) {return issue;}});data.numberOfIssues = issues.length;if (SURI_PREVIOUS) {if (JSON.parse(SURI_PREVIOUS).numberOfIssues) {data.evolution = ((data.numberOfIssues - JSON.parse(SURI_PREVIOUS).numberOfIssues) * 100 / JSON.parse(SURI_PREVIOUS).numberOfIssues).toFixed(1);} else {data.evolution = (0).toFixed(1);}data.arrow = data.evolution == 0 ? '' : (data.evolution > 0 ? \"up\" : \"down\");}if (SURI_ISSUES_STATE != 'all') {data.issuesState = SURI_ISSUES_STATE;}return JSON.stringify(data);}";

    /**
     * Mocked HTML content
     */
    private static final String HTML_CONTENT = "<div class=\"grid-stack-item-content-inner\"><h1 class=\"title\">{{SURI_GITHUB_PROJECT}}</h1><h2 class=\"value\">{{numberOfIssues}}</h2><h2 class=\"issues-label\">{{#issuesState}} {{issuesState}} {{/issuesState}} issues</h2>{{#evolution}}<p class=\"change-rate\"><i class=\"fa fa-arrow-{{arrow}}\"></i><span>{{evolution}}% since the last execution</span></p>{{/evolution}}</div><div class=\"github\"></div>";

    /**
     * Project widget repository
     */
    @Autowired
    ProjectWidgetRepository projectWidgetRepository;

    /**
     * Widget repository
     */
    @Autowired
    WidgetRepository widgetRepository;

    /**
     * Category repository
     */
    @Autowired
    CategoryRepository categoryRepository;

    /**
     * Asset repository
     */
    @Autowired
    AssetRepository assetRepository;

    /**
     * Widget service
     */
    @Autowired
    WidgetService widgetService;

    /**
     * Category service
     */
    @Autowired
    CategoryService categoryService;

    /**
     * Project widget service
     */
    @Autowired
    ProjectWidgetService projectWidgetService;

    /**
     * Library service
     */
    @Autowired
    LibraryService libraryService;

    /**
     * Library repository
     */
    @Autowired
    LibraryRepository libraryRepository;

    /**
     * Repository service
     */
    @Autowired
    RepositoryService repositoryService;

    /**
     * Repository repository
     */
    @Autowired
    RepositoryRepository repositoryRepository;

    @Test
    public void myTest() {
        Repository repository = new Repository();
        repository.setName("MyRepository");
        repository.setBranch("master");
        repository.setEnabled(true);
        repository.setLogin("Login");
        repository.setPassword("Password");
        repository.setType(RepositoryTypeEnum.REMOTE);
        repository.setUrl("https://myRepository.com");

        Asset asset = new Asset();
        asset.setContent(new byte[]{0x12});
        asset.setSize(10);

        WidgetParam countIssuesParam1 = new WidgetParam();
        countIssuesParam1.setName("SURI_GITHUB_ORG");
        countIssuesParam1.setDescription("GitHub organization");
        countIssuesParam1.setType(DataTypeEnum.TEXT);
        countIssuesParam1.setRequired(true);

        WidgetParam countIssuesParam2 = new WidgetParam();
        countIssuesParam1.setName("SURI_GITHUB_PROJECT");
        countIssuesParam1.setDescription("GitHub project");
        countIssuesParam1.setType(DataTypeEnum.TEXT);
        countIssuesParam1.setRequired(true);

        Widget countIssues = new Widget();
        countIssues.setName("Number of issues");
        countIssues.setDescription("Display the number of issues of a GitHub project");
        countIssues.setTechnicalName("githubCountIssues");
        countIssues.setDelay(600L);
        countIssues.setCssContent(CSS_CONTENT);
        countIssues.setBackendJs(BACKEND_JS);
        countIssues.setHtmlContent(HTML_CONTENT);
        countIssues.setWidgetParams(Arrays.asList(countIssuesParam1, countIssuesParam2));

        CategoryParameter gitHubConfiguration = new CategoryParameter();
        gitHubConfiguration.setKey("WIDGET_CONFIG_GITHUB_TOKEN");
        gitHubConfiguration.setValue("TEXT");

        Category gitHubCategory = new Category();
        gitHubCategory.setName("GitHub");
        gitHubCategory.setTechnicalName("github");
        gitHubCategory.setImage(asset);
        gitHubCategory.setCreatedDate(new Date());
        gitHubCategory.setConfigurations(Collections.singletonList(gitHubConfiguration));
        gitHubCategory.setWidgets(Collections.singletonList(countIssues));

        CategoryParameter gitLabConfiguration1 = new CategoryParameter();
        gitLabConfiguration1.setKey("WIDGET_CONFIG_GITLAB_TOKEN");
        gitLabConfiguration1.setValue("TEXT");

        CategoryParameter gitLabConfiguration2 = new CategoryParameter();
        gitLabConfiguration2.setKey("WIDGET_CONFIG_GITLAB_URL");
        gitLabConfiguration2.setValue("TEXT");

        Category gitLabCategory = new Category();
        gitLabCategory.setName("GitLab");
        gitLabCategory.setTechnicalName("gitlab");
        gitLabCategory.setImage(asset);
        gitHubCategory.setCreatedDate(new Date());
        gitLabCategory.setConfigurations(Arrays.asList(gitLabConfiguration1, gitLabConfiguration2));
        gitLabCategory.setWidgets(Collections.singletonList(countIssues));

        widgetService.updateWidgetInDatabase(Arrays.asList(gitHubCategory, gitLabCategory), null, null);
    }

    @Test
    public void updateStateTest() {
        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setState(WidgetStateEnum.STOPPED);
        projectWidgetRepository.save(projectWidget);
        assertThat(projectWidgetRepository.count()).isEqualTo(1);

        Date date = new Date();
        projectWidgetService.updateState(WidgetStateEnum.RUNNING, projectWidget.getId(), date);
        ProjectWidget currentPw = projectWidgetRepository.findAll().get(0);
        assertThat(currentPw.getState()).isEqualTo(WidgetStateEnum.RUNNING);
        assertThat(currentPw.getLastExecutionDate().getTime()).isEqualTo(date.getTime());
    }

    @Test
    public void addOrUpdateWidgetNullTest() {
        assertThat(widgetRepository.count()).isEqualTo(0);
        widgetService.addOrUpdateWidgets(null, null, null, null);
        assertThat(widgetRepository.count()).isEqualTo(0);
        widgetService.addOrUpdateWidgets(new Category(), null, null, null);
        assertThat(widgetRepository.count()).isEqualTo(0);
    }

    @Test
    @Transactional
    public void addOrUpdateWidgetTestImage() {
        assertThat(widgetRepository.count()).isEqualTo(0);

        // Add a category
        Category category = new Category();
        category.setName("test");
        category.setTechnicalName("test");
        categoryService.addOrUpdateCategory(category);

        // Create widget list
        Widget widget = new Widget();
        widget.setBackendJs("bakendjs");
        widget.setCssContent("cssContent");
        widget.setDelay(10L);
        widget.setDescription("Description");
        widget.setHtmlContent("HtmlContent");
        widget.setTechnicalName("widget1");
        widget.setName("Widget 1");

        // Create an asset
        Asset asset = new Asset();
        asset.setContent(new byte[]{0x12});
        asset.setSize(10);
        widget.setImage(asset);

        // Create a repository
        Repository repository = new Repository();
        repository.setName("testRepo");
        repository.setEnabled(true);
        repository.setType(RepositoryTypeEnum.LOCAL);
        repository.setLocalPath("C:/test");
        repositoryService.addOrUpdateRepository(repository);

        widgetService.addOrUpdateWidgets(category, Collections.singletonList(widget), null, repository);
        assetRepository.flush();
        widgetRepository.flush();
        repositoryRepository.flush();

        assertThat(categoryRepository.count()).isEqualTo(1);
        assertThat(widgetRepository.count()).isEqualTo(1);
        assertThat(assetRepository.count()).isEqualTo(1);
        assertThat(repositoryRepository.count()).isEqualTo(1);

        Optional<Widget> currentWidget = widgetRepository.findByTechnicalName("widget1");
        assertThat(currentWidget.isPresent()).isTrue();
        assertThat(currentWidget.get()).isNotNull();
        assertThat(currentWidget.get().getImage()).isNotNull();
        assertThat(currentWidget.get().getWidgetAvailability()).isEqualTo(WidgetAvailabilityEnum.ACTIVATED);
        assertThat(currentWidget.get().getImage().getSize()).isEqualTo(10);

        // Update image
        Asset asset1 = new Asset();
        asset1.setContent(new byte[]{0x22});

        Widget widget2 = new Widget();
        widget2.setBackendJs("bakendjs");
        widget2.setCssContent("cssContent");
        widget2.setDelay(10L);
        widget2.setDescription("Description");
        widget2.setHtmlContent("HtmlContent");
        widget2.setTechnicalName("widget1");
        widget2.setName("Widget 1");
        widget2.setImage(asset1);

        widgetService.addOrUpdateWidgets(category, Collections.singletonList(widget2), null, repository);

        assertThat(categoryRepository.count()).isEqualTo(1);
        assertThat(widgetRepository.count()).isEqualTo(1);
        assertThat(assetRepository.count()).isEqualTo(1);
        assertThat(repositoryRepository.count()).isEqualTo(1);

        currentWidget = widgetRepository.findByTechnicalName("widget1");
        assertThat(currentWidget.isPresent()).isTrue();
        assertThat(currentWidget.get()).isNotNull();
        assertThat(currentWidget.get().getWidgetAvailability()).isEqualTo(WidgetAvailabilityEnum.ACTIVATED);
        assertThat(currentWidget.get().getImage()).isNotNull();
        assertThat(currentWidget.get().getImage().getSize()).isEqualTo(1);
    }

    @Test
    @Transactional
    public void addOrUpdateWidgetTest() {
        assertThat(widgetRepository.count()).isEqualTo(0);

        // Add a category
        Category category = new Category();
        category.setName("test");
        category.setTechnicalName("test");
        categoryService.addOrUpdateCategory(category);

        // Create widget list
        Widget widget = new Widget();
        widget.setBackendJs("bakendjs");
        widget.setCssContent("cssContent");
        widget.setDelay(10L);
        widget.setDescription("Description");
        widget.setHtmlContent("HtmlContent");
        widget.setTechnicalName("widget1");
        widget.setName("Widget 1");

        Widget widget2 = new Widget();
        widget2.setBackendJs("bakendjs2");
        widget2.setCssContent("cssContent2");
        widget2.setDelay(20L);
        widget2.setDescription("Description2");
        widget2.setHtmlContent("HtmlContent2");
        widget2.setTechnicalName("widget2");
        widget2.setName("Widget 2");

        // Create a repository
        Repository repository = new Repository();
        repository.setName("testRepo");
        repository.setEnabled(true);
        repository.setType(RepositoryTypeEnum.LOCAL);
        repository.setLocalPath("C:/test");
        repositoryService.addOrUpdateRepository(repository);

        widgetService.addOrUpdateWidgets(category, Arrays.asList(widget, widget2), null, repository);

        assertThat(categoryRepository.count()).isEqualTo(1);
        assertThat(widgetRepository.count()).isEqualTo(2);
        assertThat(repositoryRepository.count()).isEqualTo(1);

        Optional<Widget> currentWidget = widgetRepository.findByTechnicalName("widget1");
        assertThat(currentWidget.isPresent()).isTrue();
        assertThat(currentWidget.get()).isNotNull();
        assertThat(currentWidget.get().getBackendJs()).isEqualTo("bakendjs");
        assertThat(EntityUtils.<Long>getProxiedId(currentWidget.get().getCategory())).isEqualTo(category.getId());
        assertThat(currentWidget.get().getCssContent()).isEqualTo("cssContent");
        assertThat(currentWidget.get().getDelay()).isEqualTo(10L);
        assertThat(currentWidget.get().getDescription()).isEqualTo("Description");
        assertThat(currentWidget.get().getHtmlContent()).isEqualTo("HtmlContent");
        assertThat(currentWidget.get().getTechnicalName()).isEqualTo("widget1");
        assertThat(currentWidget.get().getName()).isEqualTo("Widget 1");
        assertThat(currentWidget.get().getWidgetAvailability()).isEqualTo(WidgetAvailabilityEnum.ACTIVATED);

        // Change state of widget 1
        currentWidget.get().setWidgetAvailability(WidgetAvailabilityEnum.DISABLED);
        //widgetRepository.save(currentWidget.get());

        // Check widget 2
        currentWidget = widgetRepository.findByTechnicalName("widget2");
        assertThat(currentWidget.isPresent()).isTrue();
        assertThat(currentWidget.get()).isNotNull();
        assertThat(currentWidget.get().getBackendJs()).isEqualTo("bakendjs2");
        assertThat(EntityUtils.<Long>getProxiedId(currentWidget.get().getCategory())).isEqualTo(category.getId());
        assertThat(currentWidget.get().getCssContent()).isEqualTo("cssContent2");
        assertThat(currentWidget.get().getDelay()).isEqualTo(20L);
        assertThat(currentWidget.get().getDescription()).isEqualTo("Description2");
        assertThat(currentWidget.get().getHtmlContent()).isEqualTo("HtmlContent2");
        assertThat(currentWidget.get().getTechnicalName()).isEqualTo("widget2");
        assertThat(currentWidget.get().getName()).isEqualTo("Widget 2");

        // Modify widget 1
        //widget.setId(null);
        widget.setBackendJs("bakendjsModif");
        widget.setCssContent("cssContentModif");
        widget.setDelay(30L);
        widget.setDescription("DescriptionModif");
        widget.setHtmlContent("HtmlContentModif");
        widget.setTechnicalName("widget1");
        widget.setName("Widget Modif");

        //widget2.setId(null);

        widgetService.addOrUpdateWidgets(category, Arrays.asList(widget, widget2), null, repository);
        assertThat(categoryRepository.count()).isEqualTo(1);
        assertThat(widgetRepository.count()).isEqualTo(2);
        assertThat(repositoryRepository.count()).isEqualTo(1);

        currentWidget = widgetRepository.findByTechnicalName("widget1");
        assertThat(currentWidget.isPresent()).isTrue();
        assertThat(currentWidget.get()).isNotNull();
        assertThat(currentWidget.get().getWidgetAvailability()).isEqualTo(WidgetAvailabilityEnum.DISABLED);
        assertThat(currentWidget.get().getBackendJs()).isEqualTo("bakendjsModif");
        assertThat(EntityUtils.<Long>getProxiedId(currentWidget.get().getCategory())).isEqualTo(category.getId());
        assertThat(currentWidget.get().getCssContent()).isEqualTo("cssContentModif");
        assertThat(currentWidget.get().getDelay()).isEqualTo(30L);
        assertThat(currentWidget.get().getDescription()).isEqualTo("DescriptionModif");
        assertThat(currentWidget.get().getHtmlContent()).isEqualTo("HtmlContentModif");
        assertThat(currentWidget.get().getTechnicalName()).isEqualTo("widget1");
        assertThat(currentWidget.get().getName()).isEqualTo("Widget Modif");
    }

    @Test
    @Transactional
    public void addOrUpdateWidgetTestLibrary() {
        assertThat(widgetRepository.count()).isEqualTo(0);
        Asset asset = new Asset();
        asset.setContentType("test/plain");
        asset.setContent(new byte[]{0x21});

        Library lib = new Library();
        lib.setTechnicalName("lib1");
        lib.setAsset(asset);

        List<Library> libs = libraryService.updateLibraryInDatabase(Collections.singletonList(lib));
        assertThat(libs.size()).isEqualTo(1);

        // Add a category
        Category category = new Category();
        category.setName("test");
        category.setTechnicalName("test");
        categoryService.addOrUpdateCategory(category);

        // Create widget list
        Widget widget = new Widget();
        widget.setBackendJs("bakendjs");
        widget.setCssContent("cssContent");
        widget.setDelay(10L);
        widget.setDescription("Description");
        widget.setHtmlContent("HtmlContent");
        widget.setTechnicalName("widget1");
        widget.setName("Widget 1");
        widget.setLibraries(libs);

        asset = new Asset();
        asset.setContent(new byte[]{0x12});
        asset.setSize(10);
        widget.setImage(asset);

        // Create a repository
        Repository repository = new Repository();
        repository.setName("testRepo");
        repository.setEnabled(true);
        repository.setType(RepositoryTypeEnum.LOCAL);
        repository.setLocalPath("C:/test");
        repositoryService.addOrUpdateRepository(repository);

        Map<String, Library> libraryMap = libs.stream().collect(Collectors.toMap(item -> ((Library) item).getTechnicalName(), item -> item));
        widgetService.addOrUpdateWidgets(category, Collections.singletonList(widget), libraryMap, repository);

        assertThat(categoryRepository.count()).isEqualTo(1);
        assertThat(widgetRepository.count()).isEqualTo(1);
        assertThat(assetRepository.count()).isEqualTo(2);
        assertThat(libraryRepository.count()).isEqualTo(1);
        assertThat(repositoryRepository.count()).isEqualTo(1);

        Optional<Widget> currentWidget = widgetRepository.findByTechnicalName("widget1");
        assertThat(currentWidget.isPresent()).isTrue();
        assertThat(currentWidget.get()).isNotNull();
        assertThat(currentWidget.get().getImage()).isNotNull();
        assertThat(currentWidget.get().getImage().getSize()).isEqualTo(10);
        assertThat(currentWidget.get().getLibraries()).isNotNull();
        assertThat(currentWidget.get().getLibraries().get(0).getTechnicalName()).isEqualTo("lib1");
    }
}
