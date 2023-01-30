package com.michelin.suricate.services.mapper;

import com.michelin.suricate.model.dto.api.category.CategoryResponseDto;
import com.michelin.suricate.model.dto.api.widget.WidgetParamResponseDto;
import com.michelin.suricate.model.dto.api.widget.WidgetResponseDto;
import com.michelin.suricate.model.entities.*;
import com.michelin.suricate.model.enums.WidgetAvailabilityEnum;
import com.michelin.suricate.utils.IdUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WidgetMapperTest {
    @Mock
    protected WidgetParamMapper widgetParamMapper;

    @Mock
    protected CategoryMapper categoryMapper;

    @InjectMocks
    private WidgetMapperImpl widgetMapper;

    @Test
    void shouldToWidgetDTO() {
        try (MockedStatic<IdUtils> mocked = mockStatic(IdUtils.class)) {
            Asset asset = new Asset();
            asset.setId(1L);

            Category category = new Category();
            category.setId(1L);

            Repository repository = new Repository();
            repository.setId(1L);

            WidgetParam widgetParam = new WidgetParam();
            widgetParam.setId(1L);

            Widget widget = new Widget();
            widget.setId(1L);
            widget.setName("name");
            widget.setDescription("description");
            widget.setTechnicalName("technicalName");
            widget.setInfo("info");
            widget.setDelay(1L);
            widget.setCssContent("css");
            widget.setTimeout(1L);
            widget.setWidgetAvailability(WidgetAvailabilityEnum.ACTIVATED);
            widget.setImage(asset);
            widget.setCategory(category);
            widget.setRepository(repository);
            widget.setWidgetParams(Collections.singletonList(widgetParam));

            CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
            categoryResponseDto.setId(1L);

            WidgetParamResponseDto widgetParamResponseDto = new WidgetParamResponseDto();
            widgetParamResponseDto.setName("name");

            mocked.when(() -> IdUtils.encrypt(1L))
                    .thenReturn("encrypted");
            when(categoryMapper.toCategoryWithHiddenValueParametersDTO(any()))
                    .thenReturn(categoryResponseDto);
            when(widgetParamMapper.toWidgetParameterDTO(any()))
                    .thenReturn(widgetParamResponseDto);

            WidgetResponseDto actual = widgetMapper.toWidgetDTO(widget);

            assertThat(actual.getId()).isEqualTo(1L);
            assertThat(actual.getName()).isEqualTo("name");
            assertThat(actual.getDescription()).isEqualTo("description");
            assertThat(actual.getTechnicalName()).isEqualTo("technicalName");
            assertThat(actual.getInfo()).isEqualTo("info");
            assertThat(actual.getDelay()).isEqualTo(1L);
            assertThat(actual.getCssContent()).isEqualTo("css");
            assertThat(actual.getTimeout()).isEqualTo(1L);
            assertThat(actual.getWidgetAvailability()).isEqualTo(WidgetAvailabilityEnum.ACTIVATED);
            assertThat(actual.getImageToken()).isEqualTo("encrypted");
            Assertions.assertThat(actual.getImage()).isNull();
            assertThat(actual.getLibraryTechnicalNames()).isNull();
            Assertions.assertThat(actual.getCategory()).isEqualTo(categoryResponseDto);
            assertThat(actual.getRepositoryId()).isEqualTo(1L);
            assertThat(actual.getParams().get(0)).isEqualTo(widgetParamResponseDto);
        }
    }

    @Test
    void shouldToWidgetWithoutCategoryParametersDTO() {
        try (MockedStatic<IdUtils> mocked = mockStatic(IdUtils.class)) {
            Asset asset = new Asset();
            asset.setId(1L);

            Category category = new Category();
            category.setId(1L);

            Repository repository = new Repository();
            repository.setId(1L);

            WidgetParam widgetParam = new WidgetParam();
            widgetParam.setId(1L);

            Widget widget = new Widget();
            widget.setId(1L);
            widget.setName("name");
            widget.setDescription("description");
            widget.setTechnicalName("technicalName");
            widget.setInfo("info");
            widget.setDelay(1L);
            widget.setCssContent("css");
            widget.setTimeout(1L);
            widget.setWidgetAvailability(WidgetAvailabilityEnum.ACTIVATED);
            widget.setImage(asset);
            widget.setCategory(category);
            widget.setRepository(repository);
            widget.setWidgetParams(Collections.singletonList(widgetParam));

            CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
            categoryResponseDto.setId(1L);

            WidgetParamResponseDto widgetParamResponseDto = new WidgetParamResponseDto();
            widgetParamResponseDto.setName("name");

            mocked.when(() -> IdUtils.encrypt(1L))
                    .thenReturn("encrypted");
            when(categoryMapper.toCategoryWithoutParametersDTO(any()))
                    .thenReturn(categoryResponseDto);
            when(widgetParamMapper.toWidgetParameterDTO(any()))
                    .thenReturn(widgetParamResponseDto);

            WidgetResponseDto actual = widgetMapper.toWidgetWithoutCategoryParametersDTO(widget);

            assertThat(actual.getId()).isEqualTo(1L);
            assertThat(actual.getName()).isEqualTo("name");
            assertThat(actual.getDescription()).isEqualTo("description");
            assertThat(actual.getTechnicalName()).isEqualTo("technicalName");
            assertThat(actual.getInfo()).isEqualTo("info");
            assertThat(actual.getDelay()).isEqualTo(1L);
            assertThat(actual.getCssContent()).isEqualTo("css");
            assertThat(actual.getTimeout()).isEqualTo(1L);
            assertThat(actual.getWidgetAvailability()).isEqualTo(WidgetAvailabilityEnum.ACTIVATED);
            assertThat(actual.getImageToken()).isEqualTo("encrypted");
            Assertions.assertThat(actual.getImage()).isNull();
            assertThat(actual.getLibraryTechnicalNames()).isNull();
            Assertions.assertThat(actual.getCategory()).isEqualTo(categoryResponseDto);
            assertThat(actual.getRepositoryId()).isEqualTo(1L);
            assertThat(actual.getParams().get(0)).isEqualTo(widgetParamResponseDto);
        }
    }

    @Test
    void shouldToWidgetsDTOs() {
        try (MockedStatic<IdUtils> mocked = mockStatic(IdUtils.class)) {
            Asset asset = new Asset();
            asset.setId(1L);

            Category category = new Category();
            category.setId(1L);

            Repository repository = new Repository();
            repository.setId(1L);

            WidgetParam widgetParam = new WidgetParam();
            widgetParam.setId(1L);

            Widget widget = new Widget();
            widget.setId(1L);
            widget.setName("name");
            widget.setDescription("description");
            widget.setTechnicalName("technicalName");
            widget.setInfo("info");
            widget.setDelay(1L);
            widget.setCssContent("css");
            widget.setTimeout(1L);
            widget.setWidgetAvailability(WidgetAvailabilityEnum.ACTIVATED);
            widget.setImage(asset);
            widget.setCategory(category);
            widget.setRepository(repository);
            widget.setWidgetParams(Collections.singletonList(widgetParam));

            CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
            categoryResponseDto.setId(1L);

            WidgetParamResponseDto widgetParamResponseDto = new WidgetParamResponseDto();
            widgetParamResponseDto.setName("name");

            mocked.when(() -> IdUtils.encrypt(1L))
                    .thenReturn("encrypted");
            when(categoryMapper.toCategoryWithHiddenValueParametersDTO(any()))
                    .thenReturn(categoryResponseDto);
            when(widgetParamMapper.toWidgetParameterDTO(any()))
                    .thenReturn(widgetParamResponseDto);

            List<WidgetResponseDto> actual = widgetMapper.toWidgetsDTOs(Collections.singletonList(widget));

            assertThat(actual.get(0).getId()).isEqualTo(1L);
            assertThat(actual.get(0).getName()).isEqualTo("name");
            assertThat(actual.get(0).getDescription()).isEqualTo("description");
            assertThat(actual.get(0).getTechnicalName()).isEqualTo("technicalName");
            assertThat(actual.get(0).getInfo()).isEqualTo("info");
            assertThat(actual.get(0).getDelay()).isEqualTo(1L);
            assertThat(actual.get(0).getCssContent()).isEqualTo("css");
            assertThat(actual.get(0).getTimeout()).isEqualTo(1L);
            assertThat(actual.get(0).getWidgetAvailability()).isEqualTo(WidgetAvailabilityEnum.ACTIVATED);
            assertThat(actual.get(0).getImageToken()).isEqualTo("encrypted");
            Assertions.assertThat(actual.get(0).getImage()).isNull();
            assertThat(actual.get(0).getLibraryTechnicalNames()).isNull();
            Assertions.assertThat(actual.get(0).getCategory()).isEqualTo(categoryResponseDto);
            assertThat(actual.get(0).getRepositoryId()).isEqualTo(1L);
            assertThat(actual.get(0).getParams().get(0)).isEqualTo(widgetParamResponseDto);
        }
    }
}
