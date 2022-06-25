package io.suricate.monitoring.utils;

import io.suricate.monitoring.model.entities.Category;
import io.suricate.monitoring.model.entities.Library;
import io.suricate.monitoring.model.entities.Widget;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class WidgetUtilsTest {

    @Test
    public void testNullFile() throws Exception {
        assertEquals(0, WidgetUtils.parseCategoriesFolder(null).size());
    }

    @Test
    public void testWidgetFolder() throws Exception {
        List<Category> listCategory = WidgetUtils.parseCategoriesFolder(new File(FilesUtilsTest.class.getResource("/widgets/").getFile()));
        assertNotNull(listCategory);
        assertEquals(4, listCategory.size());

        assertEquals("Jira", listCategory.get(0).getName());
        assertNull(listCategory.get(0).getId());
        assertNotNull(listCategory.get(0).getImage());
        assertNotNull(listCategory.get(0).getWidgets());
        assertEquals(3, listCategory.get(0).getWidgets().size());
        assertNotNull(listCategory.get(0).getWidgets().toArray(new Widget[0])[0].getImage());
        assertEquals("test", listCategory.get(0).getWidgets().toArray(new Widget[0])[0].getInfo());
        assertFalse(listCategory.get(0).getWidgets().toArray(new Widget[0])[0].getBackendJs().isEmpty());
        assertFalse(listCategory.get(0).getWidgets().toArray(new Widget[0])[0].getHtmlContent().isEmpty());
        assertEquals("Jira count", listCategory.get(0).getWidgets().toArray(new Widget[0])[0].getName());
        assertEquals("Widget user to count the number of jira from the specified JQL query", listCategory.get(0).getWidgets().toArray(new Widget[0])[0].getDescription());
        assertEquals("jiracount", listCategory.get(0).getWidgets().toArray(new Widget[0])[0].getTechnicalName());
        assertEquals(new Long(500), listCategory.get(0).getWidgets().toArray(new Widget[0])[0].getDelay());

        assertEquals("Jira release timeline", listCategory.get(0).getWidgets().toArray(new Widget[0])[2].getName());
        assertNotNull(listCategory.get(0).getWidgets().toArray(new Widget[0])[2].getLibraries());
        assertEquals(2, listCategory.get(0).getWidgets().toArray(new Widget[0])[2].getLibraries().size());

        assertEquals("Other", listCategory.get(2).getName());
        assertNull(listCategory.get(2).getId());
        assertNull(listCategory.get(2).getImage());
        assertNotNull(listCategory.get(2).getWidgets());
        assertEquals(3, listCategory.get(2).getWidgets().size());
        assertNotNull(listCategory.get(2).getWidgets().toArray(new Widget[0])[0].getImage());
        assertNull(listCategory.get(2).getWidgets().toArray(new Widget[0])[0].getBackendJs());
        assertNull(listCategory.get(2).getWidgets().toArray(new Widget[0])[0].getInfo());
        assertFalse(listCategory.get(2).getWidgets().toArray(new Widget[0])[0].getHtmlContent().isEmpty());
        assertEquals("Clock Widget", listCategory.get(2).getWidgets().toArray(new Widget[0])[0].getName());
        assertEquals("Widget used to display current date and time", listCategory.get(2).getWidgets().toArray(new Widget[0])[0].getDescription());
        assertEquals("clock", listCategory.get(2).getWidgets().toArray(new Widget[0])[0].getTechnicalName());
        assertEquals(new Long(-1), listCategory.get(2).getWidgets().toArray(new Widget[0])[0].getDelay());

    }

    @Test
    public void testCategoryNull() throws Exception {
        assertNull(WidgetUtils.getCategory(null));
    }

    @Test
    public void testCategoryEmptyFolder() throws Exception {
        assertNull(WidgetUtils.getCategory(new File(FilesUtilsTest.class.getResource("/widgets/noWidget/").getFile())));
    }

    @Test
    public void testCategory() throws Exception {
        Category category = WidgetUtils.getCategory(new File(FilesUtilsTest.class.getResource("/widgets/jira/").getFile()));
        assertNotNull(category);

        assertEquals("Jira", category.getName());
        assertNull(category.getId());
        assertNotNull(category.getImage());
    }

    @Test
    public void testWidgetNull() throws Exception {
        assertNull(WidgetUtils.getWidget(null));
    }

    @Test
    public void testWidgetEmptyFolder() throws Exception {
        assertNull(WidgetUtils.getWidget(new File(FilesUtilsTest.class.getResource("/widgets/noWidget/").getFile())));
    }

    @Test
    public void testWidget() throws Exception {
        Widget widget = WidgetUtils.getWidget(new File(FilesUtilsTest.class.getResource("/widgets/jira/widgets/jiracount/").getFile()));
        assertNotNull(widget);

        assertEquals("Jira count", widget.getName());
        assertNull(widget.getId());
        assertEquals("test", widget.getInfo());
        assertEquals(new Long(500), widget.getDelay());
        assertEquals("Widget user to count the number of jira from the specified JQL query", widget.getDescription());
        assertEquals("jiracount", widget.getTechnicalName());
        assertFalse(widget.getHtmlContent().isEmpty());
        assertFalse(widget.getBackendJs().isEmpty());
        assertNotNull(widget.getImage());
    }

    @Test
    public void testNullLibrary() throws Exception {
        assertNull(WidgetUtils.parseLibraryFolder(null));
    }

    @Test
    public void testLibrary() throws Exception {
        List<Library> list = WidgetUtils.parseLibraryFolder(new File(FilesUtilsTest.class.getResource("/libraries/").getFile()));
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("d3.min.js", list.get(0).getTechnicalName());
        assertEquals("d32", new String(list.get(0).getAsset().getContent()));
        assertEquals("test.js", list.get(1).getTechnicalName());
        assertEquals("ok", new String(list.get(1).getAsset().getContent()));
    }
}
