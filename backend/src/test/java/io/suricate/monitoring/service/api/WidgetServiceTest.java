package io.suricate.monitoring.service.api;

import io.suricate.monitoring.model.entity.*;
import io.suricate.monitoring.model.entity.project.ProjectWidget;
import io.suricate.monitoring.model.entity.widget.Category;
import io.suricate.monitoring.model.entity.widget.Widget;
import io.suricate.monitoring.model.enums.WidgetAvailabilityEnum;
import io.suricate.monitoring.model.enums.WidgetState;
import io.suricate.monitoring.repository.*;
import io.suricate.monitoring.service.api.LibraryService;
import io.suricate.monitoring.service.api.ProjectWidgetService;
import io.suricate.monitoring.service.api.WidgetService;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class WidgetServiceTest {

    @Autowired
    ProjectWidgetRepository projectWidgetRepository;

    @Autowired
    WidgetRepository widgetRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    WidgetService widgetService;

    @Autowired
    ProjectWidgetService projectWidgetService;

    @Autowired
    LibraryService libraryService;

    @Autowired
    LibraryRepository libraryRepository;

    @Test
    public void updateStateTest(){
        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setState(WidgetState.STOPPED);
        projectWidgetRepository.save(projectWidget);
        assertThat(projectWidgetRepository.count()).isEqualTo(1);

        Date date = new Date();
        projectWidgetService.updateState(WidgetState.RUNNING,projectWidget.getId(),date);
        ProjectWidget currentPw = projectWidgetRepository.findAll().get(0);
        assertThat(currentPw.getState()).isEqualTo(WidgetState.RUNNING);
        assertThat(currentPw.getLastExecutionDate().getTime()).isEqualTo(date.getTime());
    }

    @Test
    public void addOrUpdateCategoryNullTest() {
        // check empty
        assertThat(categoryRepository.count()).isEqualTo(0);
        assertThat(assetRepository.count()).isEqualTo(0);
        widgetService.addOrUpdateCategory(null);
        assertThat(categoryRepository.count()).isEqualTo(0);
        assertThat(assetRepository.count()).isEqualTo(0);
    }

    @Test
    public void addOrUpdateCategoryTest() {
        // check empty
        assertThat(categoryRepository.count()).isEqualTo(0);
        assertThat(assetRepository.count()).isEqualTo(0);

        Category category = new Category();
        category.setName("Category 1");
        category.setTechnicalName("categ1");

        Asset asset = new Asset();
        asset.setContentType("text/plain");
        asset.setSize(10);

        category.setImage(asset);

        widgetService.addOrUpdateCategory(category);
        assertThat(category.getId()).isNotNull();
        assertThat(categoryRepository.count()).isEqualTo(1);
        assertThat(assetRepository.count()).isEqualTo(1);
        Category category1 = categoryRepository.findByTechnicalName("categ1");
        assertThat(category1).isNotNull();
        assertThat(category1.getName()).isEqualTo("Category 1");
        assertThat(category1.getTechnicalName()).isEqualTo("categ1");
        assertThat(category1.getImage().getSize()).isEqualTo(10);

        // save the same category
        category.setId(null);
        asset.setId(null);
        asset.setSize(100);

        widgetService.addOrUpdateCategory(category);
        assertThat(category.getId()).isNotNull();
        assertThat(categoryRepository.count()).isEqualTo(1);
        assertThat(assetRepository.count()).isEqualTo(1);
        category1 = categoryRepository.findByTechnicalName("categ1");
        assertThat(category1).isNotNull();
        assertThat(category1.getName()).isEqualTo("Category 1");
        assertThat(category1.getTechnicalName()).isEqualTo("categ1");
        assertThat(category1.getImage().getSize()).isEqualTo(100);

        // save an other category
        category.setName("Category 2");
        category.setTechnicalName("categ2");
        category.setId(null);
        asset.setId(null);
        asset.setSize(110);

        widgetService.addOrUpdateCategory(category);
        assertThat(category.getId()).isNotNull();
        assertThat(categoryRepository.count()).isEqualTo(2);
        assertThat(assetRepository.count()).isEqualTo(2);
        Category category2 = categoryRepository.findByTechnicalName("categ2");
        assertThat(category2).isNotNull();
        assertThat(category2.getName()).isEqualTo("Category 2");
        assertThat(category2.getTechnicalName()).isEqualTo("categ2");
        assertThat(category2.getImage().getSize()).isEqualTo(110);
    }

    @Test
    public void addOrUpdateWidgetNullTest() {
        assertThat(widgetRepository.count()).isEqualTo(0);
        widgetService.addOrUpdateWidgets(null,null, null);
        assertThat(widgetRepository.count()).isEqualTo(0);
        widgetService.addOrUpdateWidgets(new Category(),null, null);
        assertThat(widgetRepository.count()).isEqualTo(0);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void addOrUpdateWidgetTestImage() {
        assertThat(widgetRepository.count()).isEqualTo(0);

        // Add a category
        Category category = new Category();
        category.setName("test");
        category.setTechnicalName("test");
        widgetService.addOrUpdateCategory(category);

        // Create widget list
        Widget widget = new Widget();
        widget.setBackendJs("bakendjs");
        widget.setCssContent("cssContent");
        widget.setDelay(10L);
        widget.setDescription("Description");
        widget.setHtmlContent("HtmlContent");
        widget.setTechnicalName("widget1");
        widget.setName("Widget 1");

        Asset asset = new Asset();
        asset.setContent(new byte[]{0x12});
        asset.setSize(10);
        widget.setImage(asset);


        widgetService.addOrUpdateWidgets(category,Arrays.asList(widget), null);
        assetRepository.flush();
        widgetRepository.flush();

        assertThat(categoryRepository.count()).isEqualTo(1);
        assertThat(widgetRepository.count()).isEqualTo(1);
        assertThat(assetRepository.count()).isEqualTo(1);

        Widget currentWidget = widgetRepository.findByTechnicalName("widget1");
        assertThat(currentWidget).isNotNull();
        assertThat(currentWidget.getImage()).isNotNull();
        assertThat(currentWidget.getWidgetAvailability()).isEqualTo(WidgetAvailabilityEnum.ACTIVATED);
        assertThat(currentWidget.getImage().getSize()).isEqualTo(10);

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

        widgetService.addOrUpdateWidgets(category,Arrays.asList(widget2), null);

        assertThat(categoryRepository.count()).isEqualTo(1);
        assertThat(widgetRepository.count()).isEqualTo(1);
        assertThat(assetRepository.count()).isEqualTo(1);

        currentWidget = widgetRepository.findByTechnicalName("widget1");
        assertThat(currentWidget).isNotNull();
        assertThat(currentWidget.getWidgetAvailability()).isEqualTo(WidgetAvailabilityEnum.ACTIVATED);
        assertThat(currentWidget.getImage()).isNotNull();
        assertThat(currentWidget.getImage().getSize()).isEqualTo(1);
    }

    @Test
    public void addOrUpdateWidgetTest() {
        assertThat(widgetRepository.count()).isEqualTo(0);

        // Add a category
        Category category = new Category();
        category.setName("test");
        category.setTechnicalName("test");
        widgetService.addOrUpdateCategory(category);

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


        widgetService.addOrUpdateWidgets(category,Arrays.asList(widget,widget2), null);

        assertThat(categoryRepository.count()).isEqualTo(1);
        assertThat(widgetRepository.count()).isEqualTo(2);
        Widget currentWidget = widgetRepository.findByTechnicalName("widget1");
        assertThat(currentWidget).isNotNull();
        assertThat(currentWidget.getBackendJs()).isEqualTo("bakendjs");
        assertThat(EntityUtils.<Long>getProxiedId(currentWidget.getCategory())).isEqualTo(category.getId());
        assertThat(currentWidget.getCssContent()).isEqualTo("cssContent");
        assertThat(currentWidget.getDelay()).isEqualTo(10L);
        assertThat(currentWidget.getDescription()).isEqualTo("Description");
        assertThat(currentWidget.getHtmlContent()).isEqualTo("HtmlContent");
        assertThat(currentWidget.getTechnicalName()).isEqualTo("widget1");
        assertThat(currentWidget.getName()).isEqualTo("Widget 1");
        assertThat(currentWidget.getWidgetAvailability()).isEqualTo(WidgetAvailabilityEnum.ACTIVATED);

        // Change state of widget 1
        currentWidget.setWidgetAvailability(WidgetAvailabilityEnum.DISABLED);
        widgetRepository.save(currentWidget);

        // Check widget 2
        currentWidget = widgetRepository.findByTechnicalName("widget2");
        assertThat(currentWidget).isNotNull();
        assertThat(currentWidget.getBackendJs()).isEqualTo("bakendjs2");
        assertThat(EntityUtils.<Long>getProxiedId(currentWidget.getCategory())).isEqualTo(category.getId());
        assertThat(currentWidget.getCssContent()).isEqualTo("cssContent2");
        assertThat(currentWidget.getDelay()).isEqualTo(20L);
        assertThat(currentWidget.getDescription()).isEqualTo("Description2");
        assertThat(currentWidget.getHtmlContent()).isEqualTo("HtmlContent2");
        assertThat(currentWidget.getTechnicalName()).isEqualTo("widget2");
        assertThat(currentWidget.getName()).isEqualTo("Widget 2");

        // Modify widget 1
        widget.setId(null);
        widget.setBackendJs("bakendjsModif");
        widget.setCssContent("cssContentModif");
        widget.setDelay(30L);
        widget.setDescription("DescriptionModif");
        widget.setHtmlContent("HtmlContentModif");
        widget.setTechnicalName("widget1");
        widget.setName("Widget Modif");

        widget2.setId(null);

        widgetService.addOrUpdateWidgets(category,Arrays.asList(widget,widget2), null);
        assertThat(categoryRepository.count()).isEqualTo(1);
        assertThat(widgetRepository.count()).isEqualTo(2);
        currentWidget = widgetRepository.findByTechnicalName("widget1");
        assertThat(currentWidget).isNotNull();
        assertThat(currentWidget.getWidgetAvailability()).isEqualTo(WidgetAvailabilityEnum.DISABLED);
        assertThat(currentWidget.getBackendJs()).isEqualTo("bakendjsModif");
        assertThat(EntityUtils.<Long>getProxiedId(currentWidget.getCategory())).isEqualTo(category.getId());
        assertThat(currentWidget.getCssContent()).isEqualTo("cssContentModif");
        assertThat(currentWidget.getDelay()).isEqualTo(30L);
        assertThat(currentWidget.getDescription()).isEqualTo("DescriptionModif");
        assertThat(currentWidget.getHtmlContent()).isEqualTo("HtmlContentModif");
        assertThat(currentWidget.getTechnicalName()).isEqualTo("widget1");
        assertThat(currentWidget.getName()).isEqualTo("Widget Modif");
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

        List<Library> libs = libraryService.updateLibraryInDatabase(Arrays.asList(lib));
        assertThat(libs.size()).isEqualTo(1);

        // Add a category
        Category category = new Category();
        category.setName("test");
        category.setTechnicalName("test");
        widgetService.addOrUpdateCategory(category);

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

        Map<String, Library> libraryMap = libs.stream().collect(Collectors.toMap(item -> ((Library)item).getTechnicalName(), item -> item));
        widgetService.addOrUpdateWidgets(category,Arrays.asList(widget), libraryMap);

        assertThat(categoryRepository.count()).isEqualTo(1);
        assertThat(widgetRepository.count()).isEqualTo(1);
        assertThat(assetRepository.count()).isEqualTo(2);
        assertThat(libraryRepository.count()).isEqualTo(1);

        Widget currentWidget = widgetRepository.findByTechnicalName("widget1");
        assertThat(currentWidget).isNotNull();
        assertThat(currentWidget.getImage()).isNotNull();
        assertThat(currentWidget.getImage().getSize()).isEqualTo(10);
        assertThat(currentWidget.getLibraries()).isNotNull();
        assertThat(currentWidget.getLibraries().get(0).getTechnicalName()).isEqualTo("lib1");
    }
}
