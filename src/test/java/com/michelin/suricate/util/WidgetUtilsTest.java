package com.michelin.suricate.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.michelin.suricate.model.entity.Category;
import com.michelin.suricate.model.entity.CategoryParameter;
import com.michelin.suricate.model.entity.Library;
import com.michelin.suricate.model.entity.Widget;
import com.michelin.suricate.model.enumeration.DataTypeEnum;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class WidgetUtilsTest {
    @Test
    void shouldParseLibraryFolderNull() throws IOException {
        List<Library> actual = WidgetUtils.parseLibraryFolder(null);
        assertThat(actual).isNull();
    }

    @Test
    void shouldParseLibraryFolderEmpty() throws IOException {
        List<Library> actual = WidgetUtils.parseLibraryFolder(new File("src/test/resources/repository"));
        assertThat(actual).isNull();
    }

    @Test
    void shouldParseLibraryFolder() throws IOException {
        List<Library> actual = WidgetUtils.parseLibraryFolder(new File("src/test/resources/repository/libraries"));
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getTechnicalName()).isEqualTo("test.js");
    }

    @Test
    void shouldParseCategoriesEmptyFolder() {
        assertThat(
            WidgetUtils.parseCategoriesFolder(new File("src/test/resources/specific-repository/content/no-name")))
            .isEmpty();
    }

    @Test
    void shouldParseCategoriesUnknownFolder() {
        assertThat(WidgetUtils.parseCategoriesFolder(
            new File("src/test/resources/specific-repository/content/does-not-exist")))
            .isEmpty();
    }

    @Test
    void shouldParseCategoriesFolderAndGetGitHub() {
        List<Category> actual = WidgetUtils.parseCategoriesFolder(new File("src/test/resources/repository/content"));
        assertThat(actual).hasSize(3);
        assertThat(actual.get(0).getId()).isNull();
        assertThat(actual.get(0).getName()).isEqualTo("GitHub");
        assertThat(actual.get(0).getTechnicalName()).isEqualTo("github");
        assertThat(actual.get(0).getImage()).isNotNull();
        assertThat(actual.get(0).getImage().getContentType()).isEqualTo("image/png");

        List<Widget> gitHubWidgets = new ArrayList<>(actual.get(0).getWidgets());

        assertThat(gitHubWidgets).hasSize(1);
        assertThat(gitHubWidgets.get(0).getId()).isNull();
        assertThat(gitHubWidgets.get(0).getName()).isEqualTo("Number of issues");
        assertThat(gitHubWidgets.get(0).getTechnicalName()).isEqualTo("githubOpenedIssues");
        assertThat(gitHubWidgets.get(0).getDescription()).isEqualTo("Display the number of issues of a GitHub project");
        assertThat(gitHubWidgets.get(0).getDelay()).isEqualTo(600L);
        assertThat(gitHubWidgets.get(0).getHtmlContent()).isNotNull();
        assertThat(gitHubWidgets.get(0).getCssContent()).isNotNull();
        assertThat(gitHubWidgets.get(0).getBackendJs()).isNotNull();
        assertThat(gitHubWidgets.get(0).getImage()).isNotNull();
        assertThat(gitHubWidgets.get(0).getImage().getContentType()).isEqualTo("image/png");

        List<CategoryParameter> gitHubConfig = new ArrayList<>(actual.get(0).getConfigurations());

        assertThat(gitHubConfig).hasSize(1);
        assertThat(gitHubConfig.get(0).getId()).isEqualTo("WIDGET_CONFIG_GITHUB_TOKEN");
        assertThat(gitHubConfig.get(0).getValue()).isNull();
        assertThat(gitHubConfig.get(0).getDescription()).isEqualTo("Token for the GitHub API");
        assertThat(gitHubConfig.get(0).getDataType()).isEqualTo(DataTypeEnum.PASSWORD);
    }

    @Test
    void shouldParseCategoriesFolderAndGetGitLab() {
        List<Category> actual = WidgetUtils.parseCategoriesFolder(new File("src/test/resources/repository/content"));
        assertThat(actual).hasSize(3);
        assertThat(actual.get(1).getId()).isNull();
        assertThat(actual.get(1).getName()).isEqualTo("GitLab");
        assertThat(actual.get(1).getTechnicalName()).isEqualTo("gitlab");
        assertThat(actual.get(1).getImage()).isNotNull();
        assertThat(actual.get(1).getImage().getContentType()).isEqualTo("image/png");

        List<Widget> gitLabWidgets = new ArrayList<>(actual.get(1).getWidgets());

        assertThat(gitLabWidgets).hasSize(1);
        assertThat(gitLabWidgets.get(0).getId()).isNull();
        assertThat(gitLabWidgets.get(0).getName()).isEqualTo("Number of merge requests");
        assertThat(gitLabWidgets.get(0).getTechnicalName()).isEqualTo("gitlabOpenedMR");
        assertThat(gitLabWidgets.get(0).getDescription()).isEqualTo(
            "Display the number of merge requests of a GitLab project");
        assertThat(gitLabWidgets.get(0).getDelay()).isEqualTo(500L);
        assertThat(gitLabWidgets.get(0).getHtmlContent()).isNotNull();
        assertThat(gitLabWidgets.get(0).getCssContent()).isNotNull();
        assertThat(gitLabWidgets.get(0).getBackendJs()).isNotNull();
        assertThat(gitLabWidgets.get(0).getImage()).isNotNull();
        assertThat(gitLabWidgets.get(0).getImage().getContentType()).isEqualTo("image/png");

        List<CategoryParameter> gitLabConfig = new ArrayList<>(actual.get(1).getConfigurations());

        assertThat(gitLabConfig).hasSize(2);
        assertThat(gitLabConfig.stream().map(CategoryParameter::getId))
            .contains("WIDGET_CONFIG_GITLAB_URL")
            .contains("WIDGET_CONFIG_GITLAB_TOKEN");
        assertThat(gitLabConfig.stream().map(CategoryParameter::getDescription))
            .contains("URL of the GitLab environment")
            .contains("Token for the GitLab API");
    }

    @Test
    void shouldGetCategoryNull() throws IOException {
        assertThat(WidgetUtils.getCategory(null)).isNull();
    }

    @Test
    void shouldGetCategoryWithNoName() throws IOException {
        assertThat(
            WidgetUtils.getCategory(new File("src/test/resources/specific-repository/content/no-name"))).isNull();
    }

    @Test
    void shouldGetCategoryWithNoWidgets() throws IOException {
        Category actual =
            WidgetUtils.getCategory(new File("src/test/resources/specific-repository/content/no-widgets"));

        assertThat(actual.getId()).isNull();
        assertThat(actual.getName()).isEqualTo("noWidgets");
        assertThat(actual.getTechnicalName()).isEqualTo("noWidgets");
        assertThat(actual.getImage()).isNotNull();
        assertThat(actual.getImage().getContentType()).isEqualTo("image/png");
        assertThat(actual.getWidgets()).isEmpty();
    }

    @Test
    void shouldGetCategory() throws IOException {
        Category actual = WidgetUtils.getCategory(new File("src/test/resources/repository/content/github"));

        assertThat(actual.getId()).isNull();
        assertThat(actual.getName()).isEqualTo("GitHub");
        assertThat(actual.getTechnicalName()).isEqualTo("github");
        assertThat(actual.getImage()).isNotNull();
        assertThat(actual.getImage().getContentType()).isEqualTo("image/png");

        List<Widget> gitHubWidgets = new ArrayList<>(actual.getWidgets());

        assertThat(gitHubWidgets).hasSize(1);
        assertThat(gitHubWidgets.get(0).getId()).isNull();
        assertThat(gitHubWidgets.get(0).getName()).isEqualTo("Number of issues");
        assertThat(gitHubWidgets.get(0).getTechnicalName()).isEqualTo("githubOpenedIssues");
        assertThat(gitHubWidgets.get(0).getDescription()).isEqualTo("Display the number of issues of a GitHub project");
        assertThat(gitHubWidgets.get(0).getDelay()).isEqualTo(600L);
        assertThat(gitHubWidgets.get(0).getHtmlContent()).isNotNull();
        assertThat(gitHubWidgets.get(0).getCssContent()).isNotNull();
        assertThat(gitHubWidgets.get(0).getBackendJs()).isNotNull();
        assertThat(gitHubWidgets.get(0).getImage()).isNotNull();
        assertThat(gitHubWidgets.get(0).getImage().getContentType()).isEqualTo("image/png");

        List<CategoryParameter> gitHubConfig = new ArrayList<>(actual.getConfigurations());

        assertThat(gitHubConfig).hasSize(1);
        assertThat(gitHubConfig.get(0).getId()).isEqualTo("WIDGET_CONFIG_GITHUB_TOKEN");
        assertThat(gitHubConfig.get(0).getValue()).isNull();
        assertThat(gitHubConfig.get(0).getDescription()).isEqualTo("Token for the GitHub API");
        assertThat(gitHubConfig.get(0).getDataType()).isEqualTo(DataTypeEnum.PASSWORD);
    }

    @Test
    void shouldGetWidgetNull() throws IOException {
        assertThat(WidgetUtils.getWidget(null)).isNull();
    }

    @Test
    void shouldGetWidgetNoDelay() throws IOException {
        assertThat(WidgetUtils.getWidget(
                new File("src/test/resources/specific-repository/content/specific-widgets/widgets/no-delay")))
            .isNull();
    }

    @Test
    void shouldGetWidgetDelayButNoScript() throws IOException {
        assertThat(WidgetUtils.getWidget(
                new File("src/test/resources/specific-repository/content/"
                    + "specific-widgets/widgets/delay-but-no-script")))
            .isNull();
    }

    @Test
    void shouldGetWidgetNoTechnicalName() throws IOException {
        assertThat(WidgetUtils.getWidget(
                new File("src/test/resources/specific-repository/content/specific-widgets/widgets/no-technical-name")))
            .isNull();
    }

    @Test
    void shouldGetWidgetGitHubCountIssues() throws IOException {
        Widget actual =
            WidgetUtils.getWidget(new File("src/test/resources/repository/content/github/widgets/count-issues"));

        assertThat(actual.getId()).isNull();
        assertThat(actual.getName()).isEqualTo("Number of issues");
        assertThat(actual.getDescription()).isEqualTo("Display the number of issues of a GitHub project");
        assertThat(actual.getTechnicalName()).isEqualTo("githubOpenedIssues");
        assertThat(actual.getDelay()).isEqualTo(600L);
        assertThat(actual.getHtmlContent()).isNotNull();
        assertThat(actual.getCssContent()).isNotNull();
        assertThat(actual.getBackendJs()).isNotNull();
        assertThat(actual.getImage()).isNotNull();
        assertThat(actual.getWidgetParams()).hasSize(3);
    }

    @Test
    void shouldGetWidgetClockWithNoParams() throws IOException {
        Widget actual = WidgetUtils.getWidget(new File("src/test/resources/repository/content/other/widgets/clock"));

        assertThat(actual.getId()).isNull();
        assertThat(actual.getName()).isEqualTo("Clock");
        assertThat(actual.getDescription()).isEqualTo("Display the current date and time with a clock");
        assertThat(actual.getTechnicalName()).isEqualTo("clock");
        assertThat(actual.getDelay()).isEqualTo(-1L);
        assertThat(actual.getHtmlContent()).isNotNull();
        assertThat(actual.getCssContent()).isNotNull();
        assertThat(actual.getBackendJs()).isNull();
        assertThat(actual.getImage()).isNotNull();
        assertThat(actual.getWidgetParams()).isEmpty();
    }
}
