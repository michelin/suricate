package io.suricate.monitoring.services.api;

import com.google.common.collect.Lists;
import io.suricate.monitoring.model.entities.*;
import io.suricate.monitoring.model.enums.RepositoryTypeEnum;
import io.suricate.monitoring.model.enums.WidgetAvailabilityEnum;
import io.suricate.monitoring.model.enums.WidgetStateEnum;
import io.suricate.monitoring.repositories.*;
import io.suricate.monitoring.utils.EntityUtils;
import org.assertj.core.util.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Widget service test class
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class WidgetServiceTest {
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
    public void updateStateTest() {
        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setState(WidgetStateEnum.STOPPED);
        projectWidgetRepository.save(projectWidget);
        assertEquals(1, projectWidgetRepository.count());

        Date date = new Date();
        projectWidgetService.updateState(WidgetStateEnum.RUNNING, projectWidget.getId(), date);
        ProjectWidget currentPw = projectWidgetRepository.findAll().get(0);
        assertEquals(WidgetStateEnum.RUNNING, currentPw.getState());
        assertEquals(date.getTime(), currentPw.getLastExecutionDate().getTime());
    }

    @Test
    public void addOrUpdateWidgetNullTest() {
        assertEquals(0, widgetRepository.count());

        widgetService.addOrUpdateWidgets(null, null, null);
        assertEquals(0, widgetRepository.count());

        widgetService.addOrUpdateWidgets(new Category(), null, null);
        assertEquals(0, widgetRepository.count());
    }

    @Test
    @Transactional
    public void addOrUpdateWidgetTestImage() {
        assertEquals(0, widgetRepository.count());

        // Create widget list
        Widget widget = new Widget();
        widget.setBackendJs("bakendjs");
        widget.setCssContent("cssContent");
        widget.setDelay(10L);
        widget.setDescription("Description");
        widget.setHtmlContent("HtmlContent");
        widget.setTechnicalName("widget1");
        widget.setName("Widget 1");

        // Add a category
        Category category = new Category();
        category.setName("test");
        category.setTechnicalName("test");
        category.setWidgets(Collections.singleton(widget));
        categoryService.addOrUpdateCategory(category);

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

        widgetService.addOrUpdateWidgets(category, null, repository);
        assetRepository.flush();
        widgetRepository.flush();
        repositoryRepository.flush();

        assertEquals(1, categoryRepository.count());
        assertEquals(1, widgetRepository.count());
        assertEquals(1, assetRepository.count());
        assertEquals(1, repositoryRepository.count());

        Optional<Widget> currentWidget = widgetRepository.findByTechnicalName("widget1");
        assertTrue(currentWidget.isPresent());
        assertNotNull(currentWidget.get());
        assertNotNull(currentWidget.get().getImage());
        assertEquals(WidgetAvailabilityEnum.ACTIVATED, currentWidget.get().getWidgetAvailability());
        assertEquals(10, currentWidget.get().getImage().getSize());

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

        widgetService.addOrUpdateWidgets(category, null, repository);

        assertEquals(1, categoryRepository.count());
        assertEquals(1, widgetRepository.count());
        assertEquals(1, assetRepository.count());
        assertEquals(1, repositoryRepository.count());

        currentWidget = widgetRepository.findByTechnicalName("widget1");
        assertTrue(currentWidget.isPresent());
        assertNotNull(currentWidget.get());
        assertEquals(WidgetAvailabilityEnum.ACTIVATED, currentWidget.get().getWidgetAvailability());
        assertNotNull(currentWidget.get().getImage());
        assertEquals(10, currentWidget.get().getImage().getSize());
    }

    @Test
    @Transactional
    public void addOrUpdateWidgetTest() {
        assertEquals(0, widgetRepository.count());

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

        // Add a category
        Category category = new Category();
        category.setName("test");
        category.setTechnicalName("test");
        category.setWidgets(Sets.newLinkedHashSet(widget, widget2));
        categoryService.addOrUpdateCategory(category);

        // Create a repository
        Repository repository = new Repository();
        repository.setName("testRepo");
        repository.setEnabled(true);
        repository.setType(RepositoryTypeEnum.LOCAL);
        repository.setLocalPath("C:/test");
        repositoryService.addOrUpdateRepository(repository);

        widgetService.addOrUpdateWidgets(category, null, repository);

        assertEquals(1, categoryRepository.count());
        assertEquals(2, widgetRepository.count());
        assertEquals(1, repositoryRepository.count());

        Optional<Widget> currentWidget = widgetRepository.findByTechnicalName("widget1");
        assertTrue(currentWidget.isPresent());
        assertNotNull(currentWidget.get());
        assertEquals("bakendjs", currentWidget.get().getBackendJs());
        assertEquals(category.getId(), EntityUtils.<Long>getProxiedId(currentWidget.get().getCategory()));
        assertEquals("cssContent", currentWidget.get().getCssContent());
        assertEquals(10, currentWidget.get().getDelay());
        assertEquals("Description", currentWidget.get().getDescription());
        assertEquals("HtmlContent", currentWidget.get().getHtmlContent());
        assertEquals("widget1", currentWidget.get().getTechnicalName());
        assertEquals("Widget 1", currentWidget.get().getName());
        assertEquals(WidgetAvailabilityEnum.ACTIVATED, currentWidget.get().getWidgetAvailability());

        // Change state of widget 1
        currentWidget.get().setWidgetAvailability(WidgetAvailabilityEnum.DISABLED);
        //widgetRepository.save(currentWidget.get());

        // Check widget 2
        currentWidget = widgetRepository.findByTechnicalName("widget2");
        assertTrue(currentWidget.isPresent());
        assertNotNull(currentWidget.get());
        assertEquals("bakendjs2", currentWidget.get().getBackendJs());
        assertEquals(category.getId(), EntityUtils.<Long>getProxiedId(currentWidget.get().getCategory()));
        assertEquals("cssContent2", currentWidget.get().getCssContent());
        assertEquals(20L, currentWidget.get().getDelay());
        assertEquals("Description2", currentWidget.get().getDescription());
        assertEquals("HtmlContent2", currentWidget.get().getHtmlContent());
        assertEquals("widget2", currentWidget.get().getTechnicalName());
        assertEquals("Widget 2", currentWidget.get().getName());

        // Modify widget 1
        widget.setBackendJs("bakendjsModif");
        widget.setCssContent("cssContentModif");
        widget.setDelay(30L);
        widget.setDescription("DescriptionModif");
        widget.setHtmlContent("HtmlContentModif");
        widget.setTechnicalName("widget1");
        widget.setName("Widget Modif");

        //widget2.setId(null);

        widgetService.addOrUpdateWidgets(category, null, repository);
        assertEquals(1, categoryRepository.count());
        assertEquals(2, widgetRepository.count());
        assertEquals(1, repositoryRepository.count());

        currentWidget = widgetRepository.findByTechnicalName("widget1");
        assertTrue(currentWidget.isPresent());
        assertNotNull(currentWidget.get());
        assertEquals(WidgetAvailabilityEnum.DISABLED, currentWidget.get().getWidgetAvailability());
        assertEquals("bakendjsModif", currentWidget.get().getBackendJs());
        assertEquals(category.getId(), EntityUtils.<Long>getProxiedId(currentWidget.get().getCategory()));
        assertEquals("cssContentModif", currentWidget.get().getCssContent());
        assertEquals(30L, currentWidget.get().getDelay());
        assertEquals("DescriptionModif", currentWidget.get().getDescription());
        assertEquals("HtmlContentModif", currentWidget.get().getHtmlContent());
        assertEquals("widget1", currentWidget.get().getTechnicalName());
        assertEquals("Widget Modif", currentWidget.get().getName());
    }

    @Test
    @Transactional
    public void addOrUpdateWidgetTestLibrary() {
        assertEquals(0, widgetRepository.count());
        Asset asset = new Asset();
        asset.setContentType("test/plain");
        asset.setContent(new byte[]{0x21});

        Library lib = new Library();
        lib.setTechnicalName("lib1");
        lib.setAsset(asset);

        List<Library> libs = libraryService.updateLibraryInDatabase(Collections.singletonList(lib));
        assertEquals(1, libs.size());

        // Create widget list
        Widget widget = new Widget();
        widget.setBackendJs("bakendjs");
        widget.setCssContent("cssContent");
        widget.setDelay(10L);
        widget.setDescription("Description");
        widget.setHtmlContent("HtmlContent");
        widget.setTechnicalName("widget1");
        widget.setName("Widget 1");
        widget.setLibraries(Sets.newHashSet(libs));

        // Add a category
        Category category = new Category();
        category.setName("test");
        category.setTechnicalName("test");
        category.setWidgets(Collections.singleton(widget));
        categoryService.addOrUpdateCategory(category);

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

        widgetService.addOrUpdateWidgets(category, libs, repository);

        assertEquals(1, categoryRepository.count());
        assertEquals(1, widgetRepository.count());
        assertEquals(2, assetRepository.count());
        assertEquals(1, libraryRepository.count());
        assertEquals(1, repositoryRepository.count());

        Optional<Widget> currentWidget = widgetRepository.findByTechnicalName("widget1");
        assertTrue(currentWidget.isPresent());
        assertNotNull(currentWidget.get());
        assertNotNull(currentWidget.get().getImage());
        assertEquals(10, currentWidget.get().getImage().getSize());
        assertNotNull(currentWidget.get().getLibraries());
        assertEquals("lib1", Lists.newArrayList(currentWidget.get().getLibraries()).get(0).getTechnicalName());
    }
}
