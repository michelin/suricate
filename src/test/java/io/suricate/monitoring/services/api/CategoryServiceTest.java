package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.entity.Asset;
import io.suricate.monitoring.model.entity.widget.Category;
import io.suricate.monitoring.repositories.AssetRepository;
import io.suricate.monitoring.repositories.CategoryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static com.google.common.truth.Truth.assertThat;

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
        // check empty
        assertThat(categoryRepository.count()).isEqualTo(0);
        assertThat(assetRepository.count()).isEqualTo(0);
        categoryService.addOrUpdateCategory(null);
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
        asset.setContent(new byte[1]);
        asset.setContentType("text/plain");
        asset.setSize(10);

        category.setImage(asset);

        categoryService.addOrUpdateCategory(category);
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

        categoryService.addOrUpdateCategory(category);
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

        categoryService.addOrUpdateCategory(category);
        assertThat(category.getId()).isNotNull();
        assertThat(categoryRepository.count()).isEqualTo(2);
        assertThat(assetRepository.count()).isEqualTo(2);
        Category category2 = categoryRepository.findByTechnicalName("categ2");
        assertThat(category2).isNotNull();
        assertThat(category2.getName()).isEqualTo("Category 2");
        assertThat(category2.getTechnicalName()).isEqualTo("categ2");
        assertThat(category2.getImage().getSize()).isEqualTo(110);
    }
}
