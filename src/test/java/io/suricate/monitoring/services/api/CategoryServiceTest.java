package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.entities.Asset;
import io.suricate.monitoring.model.entities.Category;
import io.suricate.monitoring.repositories.AssetRepository;
import io.suricate.monitoring.repositories.CategoryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CategoryServiceTest {
    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    CategoryService categoryService;

    @Test
    public void addOrUpdateCategoryNullTest() {
        assertEquals(0, categoryRepository.count());
        assertEquals(0, assetRepository.count());

        categoryService.addOrUpdateCategory(null);

        assertEquals(0, categoryRepository.count());
        assertEquals(0, assetRepository.count());
    }

    @Test
    public void addOrUpdateCategoryTest() {
        assertEquals(0, categoryRepository.count());
        assertEquals(0, assetRepository.count());

        Category category = new Category();
        category.setName("Category 1");
        category.setTechnicalName("Technical name 1");

        Asset asset = new Asset();
        asset.setContent(new byte[1]);
        asset.setContentType("text/plain");
        asset.setSize(10);

        category.setImage(asset);

        categoryService.addOrUpdateCategory(category);

        assertNotNull(category.getId());
        assertEquals(1, categoryRepository.count());
        assertEquals(1, assetRepository.count());

        Category category1 = categoryRepository.findByTechnicalName("Technical name 1");
        assertNotNull(category1);
        assertEquals("Category 1", category1.getName());
        assertEquals("Technical name 1", category1.getTechnicalName());
        assertEquals(10, category1.getImage().getSize());

        category.setId(null);
        asset.setId(null);
        asset.setSize(100);

        categoryService.addOrUpdateCategory(category);

        assertNotNull(category.getId());
        assertEquals(1, categoryRepository.count());
        assertEquals(1, assetRepository.count());

        category1 = categoryRepository.findByTechnicalName("Technical name 1");
        assertNotNull(category1);
        assertEquals("Category 1", category1.getName());
        assertEquals("Technical name 1", category1.getTechnicalName());
        assertEquals(100, category1.getImage().getSize());

        category.setName("Category 2");
        category.setTechnicalName("Technical name 2");
        category.setId(null);
        asset.setId(null);
        asset.setSize(110);

        categoryService.addOrUpdateCategory(category);

        assertNotNull(category.getId());
        assertEquals(2, categoryRepository.count());
        assertEquals(2, assetRepository.count());

        Category category2 = categoryRepository.findByTechnicalName("Technical name 2");

        assertNotNull(category2);
        assertEquals("Category 2", category2.getName());
        assertEquals("Technical name 2", category2.getTechnicalName());
        assertEquals(110, category2.getImage().getSize());
    }
}
