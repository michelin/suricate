package io.suricate.monitoring.utils;

import io.suricate.monitoring.model.Asset;
import io.suricate.monitoring.model.Category;
import io.suricate.monitoring.model.Library;
import io.suricate.monitoring.model.Widget;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

public class WidgetUtilsTest {

//    @Test
//    public void testNullFile() throws Exception {
//        assertThat(WidgetUtils.parseWidgetFolder(null)).isNull();
//    }
//
//    @Test
//    public void testWidgetFolder() throws Exception {
//        List<Category> listCategory = WidgetUtils.parseWidgetFolder(new File(FilesUtilsTest.class.getResource("/widgets/").getFile()));
//        assertThat(listCategory).isNotNull();
//        assertThat(listCategory).hasSize(4);
//
//        assertThat(listCategory.get(0).getName()).isEqualTo("Jira");
//        assertThat(listCategory.get(0).getExplicitName()).isEqualTo(listCategory.get(0).getName());
//        assertThat(listCategory.get(0).getId()).isNull();
//        assertThat(listCategory.get(0).getImage()).isNotNull();
//        assertThat(listCategory.get(0).getWidgets()).isNotNull();
//        assertThat(listCategory.get(0).getWidgets()).hasSize(3);
//        assertThat(listCategory.get(0).getWidgets().get(0).getImage()).isNotNull();
//        assertThat(listCategory.get(0).getWidgets().get(0).getInfo()).isEqualTo("test");
//        assertThat(listCategory.get(0).getWidgets().get(0).getBackendJs()).isNotEmpty();
//        assertThat(listCategory.get(0).getWidgets().get(0).getHtmlContent()).isNotEmpty();
//        assertThat(listCategory.get(0).getWidgets().get(0).getName()).isEqualTo("Jira count");
//        assertThat(listCategory.get(0).getWidgets().get(0).getDescription()).isEqualTo("Widget user to count the number of jira from the specified JQL query");
//        assertThat(listCategory.get(0).getWidgets().get(0).getTechnicalName()).isEqualTo("jiracount");
//        assertThat(listCategory.get(0).getWidgets().get(0).getDelay()).isEqualTo(500);
//        assertThat(listCategory.get(0).getWidgets().get(2).getName()).isEqualTo("Jira release timeline");
//        assertThat(listCategory.get(0).getWidgets().get(2).getLibraries()).isNotNull();
//        assertThat(listCategory.get(0).getWidgets().get(2).getLibraries().size()).isEqualTo(2);
//        assertThat(listCategory.get(0).getWidgets().get(2).getLibraries().get(0).getTechnicalName()).isEqualTo("d3.js");
//
//        assertThat(listCategory.get(2).getName()).isEqualTo("Other");
//        assertThat(listCategory.get(2).getExplicitName()).isEqualTo(listCategory.get(2).getName());
//        assertThat(listCategory.get(2).getId()).isNull();
//        assertThat(listCategory.get(2).getImage()).isNull();
//        assertThat(listCategory.get(2).getWidgets()).isNotNull();
//        assertThat(listCategory.get(2).getWidgets()).hasSize(3);
//        assertThat(listCategory.get(2).getWidgets().get(0).getImage()).isNotNull();
//        assertThat(listCategory.get(2).getWidgets().get(0).getBackendJs()).isNull();
//        assertThat(listCategory.get(2).getWidgets().get(0).getInfo()).isNull();
//        assertThat(listCategory.get(2).getWidgets().get(0).getHtmlContent()).isNotEmpty();
//        assertThat(listCategory.get(2).getWidgets().get(0).getName()).isEqualTo("Clock Widget");
//        assertThat(listCategory.get(2).getWidgets().get(0).getDescription()).isEqualTo("Widget used to display current date and time");
//        assertThat(listCategory.get(2).getWidgets().get(0).getTechnicalName()).isEqualTo("clock");
//        assertThat(listCategory.get(2).getWidgets().get(0).getDelay()).isEqualTo(-1);
//
//    }
//
//    @Test
//    public void testCategoryNull() throws Exception {
//        assertThat(WidgetUtils.getCategory(null)).isNull();
//    }
//
//    @Test
//    public void testCategoryEmptyFolder() throws Exception {
//        assertThat(WidgetUtils.getCategory(new File(FilesUtilsTest.class.getResource("/widgets/noWidget/").getFile()))).isNull();
//    }
//
//    @Test
//    public void testCategory() throws Exception {
//        Category category = WidgetUtils.getCategory(new File(FilesUtilsTest.class.getResource("/widgets/jira/").getFile()));
//        assertThat(category).isNotNull();
//
//        assertThat(category.getName()).isEqualTo("Jira");
//        assertThat(category.getExplicitName()).isEqualTo(category.getName());
//        assertThat(category.getId()).isNull();
//        assertThat(category.getImage()).isNotNull();
//    }
//
//    @Test
//    public void testWidgetNull() throws Exception {
//        assertThat(WidgetUtils.getWidget(null)).isNull();
//    }
//
//    @Test
//    public void testWidgetEmptyFolder() throws Exception {
//        assertThat(WidgetUtils.getWidget(new File(FilesUtilsTest.class.getResource("/widgets/noWidget/").getFile()))).isNull();
//    }
//
//    @Test
//    public void testWidget() throws Exception {
//        Widget widget = WidgetUtils.getWidget(new File(FilesUtilsTest.class.getResource("/widgets/jira/widgets/jiracount/").getFile()));
//        assertThat(widget).isNotNull();
//
//        assertThat(widget.getName()).isEqualTo("Jira count");
//        assertThat(widget.getExplicitName()).isEqualTo(widget.getName());
//        assertThat(widget.getId()).isNull();
//        assertThat(widget.getInfo()).isEqualTo("test");
//        assertThat(widget.getDelay()).isEqualTo(500);
//        assertThat(widget.getDescription()).isEqualTo("Widget user to count the number of jira from the specified JQL query");
//        assertThat(widget.getTechnicalName()).isEqualTo("jiracount");
//        assertThat(widget.getHtmlContent()).isNotEmpty();
//        assertThat(widget.getBackendJs()).isNotEmpty();
//        assertThat(widget.getImage()).isNotNull();
//    }
//
//    @Test
//    public void testNullLibrary() throws Exception {
//        assertThat(WidgetUtils.parseLibraryFolder(null)).isNull();
//    }
//
//    @Test
//    public void testLibrary() throws Exception {
//        List<Library> list = WidgetUtils.parseLibraryFolder(new File(FilesUtilsTest.class.getResource("/Libraries/").getFile()));
//        assertThat(list).isNotNull();
//        assertThat(list.size()).isEqualTo(2);
//        assertThat(list.get(0).getExplicitName()).isEqualTo("d3.min.js");
//        assertThat(new String(list.get(0).getAsset().getContent())).isEqualTo("d32");
//        assertThat(list.get(1).getExplicitName()).isEqualTo("test.js");
//        assertThat(new String(list.get(1).getAsset().getContent())).isEqualTo("ok");
//    }
//
//    @Test
//    public void testReadAsset() throws Exception {
//        Asset asset = WidgetUtils.readAsset(new File(FilesUtilsTest.class.getResource("/Libraries/d3.min.js").getFile()));
//        assertThat(asset).isNotNull();
//        assertThat(asset.getContentType()).isEqualTo("application/javascript");
//        assertThat(asset.getSize()).isEqualTo(3);
//    }
//
//    @Test
//    public void testReadImageAsset() throws Exception {
//        Asset asset = WidgetUtils.readAsset(new File(FilesUtilsTest.class.getResource("/widgets/jira/icon.jpeg").getFile()));
//        assertThat(asset).isNotNull();
//        assertThat(asset.getContentType()).isEqualTo("image/jpeg");
//        assertThat(asset.getSize()).isEqualTo(39201);
//    }
}
