package com.michelin.suricate.services.mapper;

import com.michelin.suricate.model.dto.api.category.CategoryParameterResponseDto;
import com.michelin.suricate.model.dto.api.category.CategoryResponseDto;
import com.michelin.suricate.model.entities.Asset;
import com.michelin.suricate.model.entities.Category;
import com.michelin.suricate.model.entities.CategoryParameter;
import com.michelin.suricate.model.entities.Widget;
import com.michelin.suricate.model.enums.DataTypeEnum;
import com.michelin.suricate.utils.IdUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class CategoryMapperTest {
    @InjectMocks
    private CategoryMapperImpl categoryMapper;

    @Test
    void shouldToCategoryWithoutParametersDTO() {
        try (MockedStatic<IdUtils> mocked = mockStatic(IdUtils.class)) {
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

            mocked.when(() -> IdUtils.encrypt(1L))
                    .thenReturn("encrypted");

            CategoryResponseDto actual = categoryMapper.toCategoryWithoutParametersDTO(category);

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
    void shouldToCategoryWithHiddenValueParametersDTO() {
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

            CategoryResponseDto actual = categoryMapper.toCategoryWithHiddenValueParametersDTO(category);

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
    void shouldToCategoryParameterDTO() {
        try (MockedStatic<IdUtils> mocked = mockStatic(IdUtils.class)) {
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

            mocked.when(() -> IdUtils.encrypt(1L))
                    .thenReturn("encrypted");

            CategoryParameterResponseDto actual = categoryMapper.toCategoryParameterDTO(categoryParameter);

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
    void shouldToCategoryParameterWithHiddenValuesDTO() {
        try (MockedStatic<IdUtils> mocked = mockStatic(IdUtils.class)) {
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

            mocked.when(() -> IdUtils.encrypt(1L))
                    .thenReturn("encrypted");

            CategoryParameterResponseDto actual = categoryMapper.toCategoryParameterWithHiddenValuesDTO(categoryParameter);

            assertThat(actual.getKey()).isEqualTo("key");
            assertThat(actual.getValue()).isEqualTo("value");
            Assertions.assertThat(actual.getDataType()).isEqualTo(DataTypeEnum.TEXT);
            assertThat(actual.isExport()).isTrue();
            assertThat(actual.getDescription()).isEqualTo("description");
            assertThat(actual.getCategory()).isNull();
        }
    }
}
