package com.michelin.suricate.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import com.michelin.suricate.model.dto.api.category.CategoryParameterResponseDto;
import com.michelin.suricate.model.dto.api.category.CategoryResponseDto;
import com.michelin.suricate.model.entity.Asset;
import com.michelin.suricate.model.entity.Category;
import com.michelin.suricate.model.entity.CategoryParameter;
import com.michelin.suricate.model.entity.Widget;
import com.michelin.suricate.model.enumeration.DataTypeEnum;
import com.michelin.suricate.util.IdUtils;
import java.util.Collections;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryMapperTest {
    @InjectMocks
    private CategoryMapperImpl categoryMapper;

    @Test
    void shouldToCategoryWithoutParametersDto() {
        try (MockedStatic<IdUtils> mocked = mockStatic(IdUtils.class)) {
            Category category = getCategory();

            mocked.when(() -> IdUtils.encrypt(1L))
                .thenReturn("encrypted");

            CategoryResponseDto actual = categoryMapper.toCategoryWithoutParametersDto(category);

            assertThat(actual.getId()).isEqualTo(1L);
            assertThat(actual.getName()).isEqualTo("name");
            assertThat(actual.getTechnicalName()).isEqualTo("technicalName");
            assertThat(actual.getAssetToken()).isEqualTo("encrypted");
            assertThat(actual.getImage()).isNull();
            assertThat(actual.getCategoryParameters()).isNull();
            assertThat(actual.getWidgets()).isEmpty();
        }
    }

    @Test
    void shouldToCategoryWithHiddenValueParametersDto() {
        try (MockedStatic<IdUtils> mocked = mockStatic(IdUtils.class)) {
            CategoryParameter categoryParameter = new CategoryParameter();
            categoryParameter.setKey("key");
            categoryParameter.setValue("value");
            categoryParameter.setDataType(DataTypeEnum.TEXT);
            categoryParameter.setExport(true);
            categoryParameter.setDescription("description");

            Asset asset = new Asset();
            asset.setId(1L);

            Widget widget = new Widget();
            widget.setId(1L);

            Category category = new Category();
            category.setId(1L);
            category.setName("name");
            category.setTechnicalName("technicalName");
            category.setImage(asset);
            category.setWidgets(Collections.singleton(widget));
            category.setConfigurations(Collections.singleton(categoryParameter));

            mocked.when(() -> IdUtils.encrypt(1L))
                .thenReturn("encrypted");

            CategoryResponseDto actual = categoryMapper.toCategoryWithHiddenValueParametersDto(category);

            assertThat(actual.getId()).isEqualTo(1L);
            assertThat(actual.getName()).isEqualTo("name");
            assertThat(actual.getTechnicalName()).isEqualTo("technicalName");
            assertThat(actual.getAssetToken()).isEqualTo("encrypted");
            assertThat(actual.getImage()).isNull();

            CategoryParameterResponseDto categoryParameterResponseDto = new CategoryParameterResponseDto();
            categoryParameterResponseDto.setKey("key");
            categoryParameterResponseDto.setValue("value");
            categoryParameterResponseDto.setDataType(DataTypeEnum.TEXT);
            categoryParameterResponseDto.setExport(true);
            categoryParameterResponseDto.setDescription("description");
            assertThat(actual.getCategoryParameters()).contains(categoryParameterResponseDto);
            assertThat(actual.getWidgets()).isEmpty();
        }
    }

    @Test
    void shouldToCategoryParameterDto() {
        try (MockedStatic<IdUtils> mocked = mockStatic(IdUtils.class)) {
            CategoryParameter categoryParameter = getCategoryParameter();

            mocked.when(() -> IdUtils.encrypt(1L))
                .thenReturn("encrypted");

            CategoryParameterResponseDto actual = categoryMapper.toCategoryParameterDto(categoryParameter);

            assertThat(actual.getKey()).isEqualTo("key");
            assertThat(actual.getValue()).isEqualTo("value");
            Assertions.assertThat(actual.getDataType()).isEqualTo(DataTypeEnum.TEXT);
            assertThat(actual.isExport()).isTrue();
            assertThat(actual.getDescription()).isEqualTo("description");

            CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
            categoryResponseDto.setId(1L);
            categoryResponseDto.setName("name");
            categoryResponseDto.setTechnicalName("technicalName");
            categoryResponseDto.setAssetToken("encrypted");
            assertThat(actual.getCategory()).isEqualTo(categoryResponseDto);
        }
    }

    @Test
    void shouldToCategoryParameterWithHiddenValuesDto() {
        try (MockedStatic<IdUtils> mocked = mockStatic(IdUtils.class)) {
            CategoryParameter categoryParameter = getCategoryParameter();

            mocked.when(() -> IdUtils.encrypt(1L))
                .thenReturn("encrypted");

            CategoryParameterResponseDto actual =
                categoryMapper.toCategoryParameterWithHiddenValuesDto(categoryParameter);

            assertThat(actual.getKey()).isEqualTo("key");
            assertThat(actual.getValue()).isEqualTo("value");
            Assertions.assertThat(actual.getDataType()).isEqualTo(DataTypeEnum.TEXT);
            assertThat(actual.isExport()).isTrue();
            assertThat(actual.getDescription()).isEqualTo("description");
            assertThat(actual.getCategory()).isNull();
        }
    }

    @NotNull
    private static Category getCategory() {
        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");

        Asset asset = new Asset();
        asset.setId(1L);

        Widget widget = new Widget();
        widget.setId(1L);

        Category category = new Category();
        category.setId(1L);
        category.setName("name");
        category.setTechnicalName("technicalName");
        category.setImage(asset);
        category.setWidgets(Collections.singleton(widget));
        category.setConfigurations(Collections.singleton(categoryParameter));
        return category;
    }
    
    @NotNull
    private static CategoryParameter getCategoryParameter() {
        Asset asset = new Asset();
        asset.setId(1L);

        Category category = new Category();
        category.setId(1L);
        category.setName("name");
        category.setTechnicalName("technicalName");
        category.setImage(asset);

        CategoryParameter categoryParameter = new CategoryParameter();
        categoryParameter.setKey("key");
        categoryParameter.setValue("value");
        categoryParameter.setDataType(DataTypeEnum.TEXT);
        categoryParameter.setExport(true);
        categoryParameter.setDescription("description");
        categoryParameter.setCategory(category);
        return categoryParameter;
    }
}
