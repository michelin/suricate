package com.michelin.suricate.services.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entities.Asset;
import com.michelin.suricate.model.entities.Category;
import com.michelin.suricate.model.entities.CategoryParameter;
import com.michelin.suricate.model.entities.Category_;
import com.michelin.suricate.model.entities.Widget;
import com.michelin.suricate.model.entities.WidgetParam;
import com.michelin.suricate.model.enums.DataTypeEnum;
import com.michelin.suricate.repositories.CategoryRepository;
import com.michelin.suricate.services.specifications.CategorySearchSpecification;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.metamodel.SingularAttribute;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private AssetService assetService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryParametersService categoryParametersService;

    @Mock
    private SingularAttribute<Category, String> name;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void shouldGetAll() {
        Category category = new Category();
        category.setId(1L);
        category.setName("name");

        Category_.name = name;
        when(categoryRepository.findAll(any(CategorySearchSpecification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(category)));

        Page<Category> actual = categoryService.getAll("search", Pageable.unpaged());

        assertThat(actual).isNotEmpty();
        assertThat(actual.get()).hasSize(1);

        verify(categoryRepository)
            .findAll(Mockito.<CategorySearchSpecification>argThat(
                    specification -> specification.getSearch().equals("search")
                        && specification.getAttributes().contains(name.getName())),
                Mockito.<Pageable>argThat(pageable -> pageable.equals(Pageable.unpaged())));
    }

    @Test
    void shouldFindByTechnicalName() {
        Category category = new Category();
        category.setId(1L);
        category.setName("name");
        category.setTechnicalName("technicalName");

        when(categoryRepository.findByTechnicalName("technicalName"))
            .thenReturn(category);

        Category actual = categoryRepository.findByTechnicalName("technicalName");

        assertThat(actual)
            .isNotNull()
            .isEqualTo(category);

        verify(categoryRepository)
            .findByTechnicalName("technicalName");
    }

    @Test
    void shouldAddCategoryWhenNull() {
        categoryService.addOrUpdateCategory(null);

        verify(assetService, times(0))
            .save(any());
        verify(categoryRepository, times(0))
            .save(any());
        verify(categoryParametersService, times(0))
            .deleteOneByKey(any());
        verify(categoryParametersService, times(0))
            .addOrUpdateCategoryConfiguration(any(), any());
    }

    @Test
    void shouldAddCategory() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");

        Asset asset = new Asset();
        asset.setId(1L);

        Category category = new Category();
        category.setName("name");
        category.setTechnicalName("technicalName");
        category.setImage(asset);
        category.setConfigurations(Collections.singleton(categoryParameter));

        when(categoryRepository.findByTechnicalName("technicalName"))
            .thenReturn(null);

        categoryService.addOrUpdateCategory(category);

        verify(assetService)
            .save(asset);
        verify(categoryRepository)
            .save(category);
        verify(categoryParametersService, times(0))
            .deleteOneByKey("key");
        verify(categoryParametersService)
            .addOrUpdateCategoryConfiguration(Collections.singleton(categoryParameter), category);
    }

    @Test
    void shouldAddCategoryNoAssetNoConfig() {
        Category category = new Category();
        category.setName("name");
        category.setTechnicalName("technicalName");

        when(categoryRepository.findByTechnicalName("technicalName"))
            .thenReturn(null);

        categoryService.addOrUpdateCategory(category);

        verify(assetService, times(0))
            .save(any());
        verify(categoryRepository)
            .save(category);
        verify(categoryParametersService, times(0))
            .deleteOneByKey(any());
        verify(categoryParametersService, times(0))
            .addOrUpdateCategoryConfiguration(any(), any());
    }

    @Test
    void shouldUpdateCategory() {
        Asset oldAsset = new Asset();
        oldAsset.setId(1L);

        CategoryParameter oldCategoryParameter = new CategoryParameter();
        oldCategoryParameter.setKey("oldKey");

        Category oldCategory = new Category();
        oldCategory.setId(2L);
        oldCategory.setName("oldName");
        oldCategory.setTechnicalName("oldTechnicalName");
        oldCategory.setImage(oldAsset);
        oldCategory.setConfigurations(Collections.singleton(oldCategoryParameter));

        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");

        Asset asset = new Asset();
        asset.setId(1L);

        Category category = new Category();
        category.setName("name");
        category.setTechnicalName("technicalName");
        category.setImage(asset);
        category.setConfigurations(Collections.singleton(categoryParameter));

        when(categoryRepository.findByTechnicalName("technicalName"))
            .thenReturn(oldCategory);

        categoryService.addOrUpdateCategory(category);

        assertThat(category.getId()).isEqualTo(2L);
        assertThat(category.getImage().getId()).isEqualTo(1L);

        verify(assetService)
            .save(asset);
        verify(categoryRepository)
            .save(category);
        verify(categoryParametersService)
            .deleteOneByKey("oldKey");
        verify(categoryParametersService)
            .addOrUpdateCategoryConfiguration(Collections.singleton(categoryParameter), category);
    }

    @Test
    void shouldGetCategoryParametersByWidget() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");
        categoryParameter.setValue("value");
        categoryParameter.setDataType(DataTypeEnum.TEXT);

        Category category = new Category();
        category.setId(1L);
        category.setName("name");
        category.setTechnicalName("technicalName");

        Widget widget = new Widget();
        widget.setCategory(category);

        when(categoryParametersService.getParametersByCategoryId(any()))
            .thenReturn(Optional.of(Collections.singletonList(categoryParameter)));

        List<WidgetParam> actual = categoryService.getCategoryParametersByWidget(widget);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getName()).isEqualTo("key");
        assertThat(actual.get(0).getDefaultValue()).isEqualTo("value");
        assertThat(actual.get(0).getType()).isEqualTo(DataTypeEnum.TEXT);
        assertThat(actual.get(0).getDescription()).isEqualTo("key");
        assertThat(actual.get(0).isRequired()).isTrue();
    }
}
