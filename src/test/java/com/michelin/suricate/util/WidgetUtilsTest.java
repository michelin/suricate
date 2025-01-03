package com.michelin.suricate.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertNull(actual);
    }

    @Test
    void shouldParseLibraryFolderEmpty() throws IOException {
        List<Library> actual = WidgetUtils.parseLibraryFolder(new File("src/test/resources/repository"));
        assertNull(actual);
    }

    @Test
    void shouldParseLibraryFolder() throws IOException {
        List<Library> actual = WidgetUtils.parseLibraryFolder(new File("src/test/resources/repository/libraries"));
        assertEquals(1, actual.size());
        assertEquals("test.js", actual.getFirst().getTechnicalName());
    }

    @Test
    void shouldParseCategoriesEmptyFolder() {
        assertTrue(WidgetUtils
            .parseCategoriesFolder(new File("src/test/resources/specific-repository/content/no-name"))
            .isEmpty());
    }

    @Test
    void shouldParseCategoriesUnknownFolder() {
        assertTrue(WidgetUtils
            .parseCategoriesFolder(new File("src/test/resources/specific-repository/content/does-not-exist"))
            .isEmpty());
    }

    @Test
    void shouldParseCategoriesFolderAndGetGitHub() {
        List<Category> actual = WidgetUtils.parseCategoriesFolder(new File("src/test/resources/repository/content"));

        assertEquals(3, actual.size());
        assertNull(actual.getFirst().getId());
        assertEquals("GitHub", actual.getFirst().getName());
        assertEquals("github", actual.getFirst().getTechnicalName());
        assertNotNull(actual.getFirst().getImage());
        assertEquals("image/png", actual.getFirst().getImage().getContentType());

        List<Widget> gitHubWidgets = new ArrayList<>(actual.getFirst().getWidgets());

        assertEquals(1, gitHubWidgets.size());
        assertNull(gitHubWidgets.getFirst().getId());
        assertEquals("Number of issues", gitHubWidgets.getFirst().getName());
        assertEquals("githubOpenedIssues", gitHubWidgets.getFirst().getTechnicalName());
        assertEquals("Display the number of issues of a GitHub project", gitHubWidgets.getFirst().getDescription());
        assertEquals(600L, gitHubWidgets.getFirst().getDelay());
        assertNotNull(gitHubWidgets.getFirst().getHtmlContent());
        assertNotNull(gitHubWidgets.getFirst().getCssContent());
        assertNotNull(gitHubWidgets.getFirst().getBackendJs());
        assertNotNull(gitHubWidgets.getFirst().getImage());
        assertEquals("image/png", gitHubWidgets.getFirst().getImage().getContentType());

        List<CategoryParameter> gitHubConfig = new ArrayList<>(actual.getFirst().getConfigurations());

        assertEquals(1, gitHubConfig.size());
        assertEquals("WIDGET_CONFIG_GITHUB_TOKEN", gitHubConfig.getFirst().getId());
        assertNull(gitHubConfig.getFirst().getValue());
        assertEquals("Token for the GitHub API", gitHubConfig.getFirst().getDescription());
        assertEquals(DataTypeEnum.PASSWORD, gitHubConfig.getFirst().getDataType());
    }

    @Test
    void shouldParseCategoriesFolderAndGetGitLab() {
        List<Category> actual = WidgetUtils.parseCategoriesFolder(new File("src/test/resources/repository/content"));

        assertEquals(3, actual.size());
        assertNull(actual.get(1).getId());
        assertEquals("GitLab", actual.get(1).getName());
        assertEquals("gitlab", actual.get(1).getTechnicalName());
        assertNotNull(actual.get(1).getImage());
        assertEquals("image/png", actual.get(1).getImage().getContentType());

        List<Widget> gitLabWidgets = new ArrayList<>(actual.get(1).getWidgets());

        assertEquals(1, gitLabWidgets.size());
        assertNull(gitLabWidgets.getFirst().getId());
        assertEquals("Number of merge requests", gitLabWidgets.getFirst().getName());
        assertEquals("gitlabOpenedMR", gitLabWidgets.getFirst().getTechnicalName());
        assertEquals("Display the number of merge requests of a GitLab project",
            gitLabWidgets.getFirst().getDescription());
        assertEquals(500L, gitLabWidgets.getFirst().getDelay());
        assertNotNull(gitLabWidgets.getFirst().getHtmlContent());
        assertNotNull(gitLabWidgets.getFirst().getCssContent());
        assertNotNull(gitLabWidgets.getFirst().getBackendJs());
        assertNotNull(gitLabWidgets.getFirst().getImage());
        assertEquals("image/png", gitLabWidgets.getFirst().getImage().getContentType());

        List<CategoryParameter> gitLabConfig = new ArrayList<>(actual.get(1).getConfigurations());

        assertEquals(2, gitLabConfig.size());
        assertIterableEquals(List.of("WIDGET_CONFIG_GITLAB_URL", "WIDGET_CONFIG_GITLAB_TOKEN"),
            gitLabConfig.stream().map(CategoryParameter::getId).toList());
        assertIterableEquals(List.of("URL of the GitLab environment", "Token for the GitLab API"),
            gitLabConfig.stream().map(CategoryParameter::getDescription).toList());
    }

    @Test
    void shouldGetCategoryNull() throws IOException {
        assertNull(WidgetUtils.getCategory(null));
    }

    @Test
    void shouldGetCategoryWithNoName() throws IOException {
        assertNull(WidgetUtils
            .getCategory(new File("src/test/resources/specific-repository/content/no-name")));
    }

    @Test
    void shouldGetCategoryWithNoWidgets() throws IOException {
        Category actual = WidgetUtils
            .getCategory(new File("src/test/resources/specific-repository/content/no-widgets"));

        assertNull(actual.getId());
        assertEquals("noWidgets", actual.getName());
        assertEquals("noWidgets", actual.getTechnicalName());
        assertNotNull(actual.getImage());
        assertEquals("image/png", actual.getImage().getContentType());
        assertTrue(actual.getWidgets().isEmpty());
    }

    @Test
    void shouldGetCategory() throws IOException {
        Category actual = WidgetUtils.getCategory(new File("src/test/resources/repository/content/github"));

        assertNull(actual.getId());
        assertEquals("GitHub", actual.getName());
        assertEquals("github", actual.getTechnicalName());
        assertNotNull(actual.getImage());
        assertEquals("image/png", actual.getImage().getContentType());

        List<Widget> gitHubWidgets = new ArrayList<>(actual.getWidgets());

        assertEquals(1, gitHubWidgets.size());
        assertNull(gitHubWidgets.getFirst().getId());
        assertEquals("Number of issues", gitHubWidgets.getFirst().getName());
        assertEquals("githubOpenedIssues", gitHubWidgets.getFirst().getTechnicalName());
        assertEquals("Display the number of issues of a GitHub project", gitHubWidgets.getFirst().getDescription());
        assertEquals(600L, gitHubWidgets.getFirst().getDelay());
        assertNotNull(gitHubWidgets.getFirst().getHtmlContent());
        assertNotNull(gitHubWidgets.getFirst().getCssContent());
        assertNotNull(gitHubWidgets.getFirst().getBackendJs());
        assertNotNull(gitHubWidgets.getFirst().getImage());
        assertEquals("image/png", gitHubWidgets.getFirst().getImage().getContentType());

        List<CategoryParameter> gitHubConfig = new ArrayList<>(actual.getConfigurations());

        assertEquals(1, gitHubConfig.size());
        assertEquals("WIDGET_CONFIG_GITHUB_TOKEN", gitHubConfig.getFirst().getId());
        assertNull(gitHubConfig.getFirst().getValue());
        assertEquals("Token for the GitHub API", gitHubConfig.getFirst().getDescription());
        assertEquals(DataTypeEnum.PASSWORD, gitHubConfig.getFirst().getDataType());
    }

    @Test
    void shouldGetWidgetNull() throws IOException {
        assertNull(WidgetUtils.getWidget(null));
    }

    @Test
    void shouldGetWidgetNoDelay() throws IOException {
        assertNull(WidgetUtils
            .getWidget(new File("src/test/resources/specific-repository/content/specific-widgets/widgets/no-delay")));
    }

    @Test
    void shouldGetWidgetDelayButNoScript() throws IOException {
        assertNull(WidgetUtils
            .getWidget(new File("src/test/resources/specific-repository/content/"
                    + "specific-widgets/widgets/delay-but-no-script")));
    }

    @Test
    void shouldGetWidgetNoTechnicalName() throws IOException {
        assertNull(WidgetUtils
            .getWidget(
                new File("src/test/resources/specific-repository/content/specific-widgets/widgets/no-technical-name")));
    }

    @Test
    void shouldGetWidgetGitHubCountIssues() throws IOException {
        Widget actual = WidgetUtils
            .getWidget(new File("src/test/resources/repository/content/github/widgets/count-issues"));

        assertNull(actual.getId());
        assertEquals("Number of issues", actual.getName());
        assertEquals("Display the number of issues of a GitHub project", actual.getDescription());
        assertEquals("githubOpenedIssues", actual.getTechnicalName());
        assertEquals(600L, actual.getDelay());
        assertNotNull(actual.getHtmlContent());
        assertNotNull(actual.getCssContent());
        assertNotNull(actual.getBackendJs());
        assertNotNull(actual.getImage());
        assertEquals(3, actual.getWidgetParams().size());
    }

    @Test
    void shouldGetWidgetClockWithNoParams() throws IOException {
        Widget actual = WidgetUtils.getWidget(new File("src/test/resources/repository/content/other/widgets/clock"));

        assertNull(actual.getId());
        assertEquals("Clock", actual.getName());
        assertEquals("Display the current date and time with a clock", actual.getDescription());
        assertEquals("clock", actual.getTechnicalName());
        assertEquals(-1L, actual.getDelay());
        assertNotNull(actual.getHtmlContent());
        assertNotNull(actual.getCssContent());
        assertNull(actual.getBackendJs());
        assertNotNull(actual.getImage());
        assertTrue(actual.getWidgetParams().isEmpty());
    }
}
