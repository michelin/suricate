package com.michelin.suricate.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.category.CategoryResponseDto;
import com.michelin.suricate.model.dto.api.widget.WidgetParamResponseDto;
import com.michelin.suricate.model.dto.api.widget.WidgetResponseDto;
import com.michelin.suricate.model.entity.Asset;
import com.michelin.suricate.model.entity.Category;
import com.michelin.suricate.model.entity.Repository;
import com.michelin.suricate.model.entity.Widget;
import com.michelin.suricate.model.entity.WidgetParam;
import com.michelin.suricate.model.enumeration.WidgetAvailabilityEnum;
import com.michelin.suricate.util.IdUtils;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WidgetMapperTest {
    @Mock
    protected WidgetParamMapper widgetParamMapper;

    @Mock
    protected CategoryMapper categoryMapper;

    @InjectMocks
    private WidgetMapperImpl widgetMapper;

    @Test
    void shouldConvertToWidgetDto() {
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
            when(categoryMapper.toCategoryWithHiddenValueParametersDto(any()))
                .thenReturn(categoryResponseDto);
            when(widgetParamMapper.toWidgetParameterDto(any()))
                .thenReturn(widgetParamResponseDto);

            WidgetResponseDto actual = widgetMapper.toWidgetDto(widget);

            assertEquals(1L, actual.getId());
            assertEquals("name", actual.getName());
            assertEquals("description", actual.getDescription());
            assertEquals("technicalName", actual.getTechnicalName());
            assertEquals("info", actual.getInfo());
            assertEquals(1L, actual.getDelay());
            assertEquals("css", actual.getCssContent());
            assertEquals(1L, actual.getTimeout());
            assertEquals(WidgetAvailabilityEnum.ACTIVATED, actual.getWidgetAvailability());
            assertEquals("encrypted", actual.getImageToken());
            assertNull(actual.getImage());
            assertNull(actual.getLibraryTechnicalNames());
            assertEquals(categoryResponseDto, actual.getCategory());
            assertEquals(1L, actual.getRepositoryId());
            assertEquals(widgetParamResponseDto, actual.getParams().getFirst());
        }
    }

    @Test
    void shouldConvertToWidgetWithoutCategoryParametersDto() {
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
            when(categoryMapper.toCategoryWithoutParametersDto(any()))
                .thenReturn(categoryResponseDto);
            when(widgetParamMapper.toWidgetParameterDto(any()))
                .thenReturn(widgetParamResponseDto);

            WidgetResponseDto actual = widgetMapper.toWidgetWithoutCategoryParametersDto(widget);

            assertEquals(1L, actual.getId());
            assertEquals("name", actual.getName());
            assertEquals("description", actual.getDescription());
            assertEquals("technicalName", actual.getTechnicalName());
            assertEquals("info", actual.getInfo());
            assertEquals(1L, actual.getDelay());
            assertEquals("css", actual.getCssContent());
            assertEquals(1L, actual.getTimeout());
            assertEquals(WidgetAvailabilityEnum.ACTIVATED, actual.getWidgetAvailability());
            assertEquals("encrypted", actual.getImageToken());
            assertNull(actual.getImage());
            assertNull(actual.getLibraryTechnicalNames());
            assertEquals(categoryResponseDto, actual.getCategory());
            assertEquals(1L, actual.getRepositoryId());
            assertEquals(widgetParamResponseDto, actual.getParams().getFirst());
        }
    }

    @Test
    void shouldConvertToWidgetsDtos() {
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
            when(categoryMapper.toCategoryWithHiddenValueParametersDto(any()))
                .thenReturn(categoryResponseDto);
            when(widgetParamMapper.toWidgetParameterDto(any()))
                .thenReturn(widgetParamResponseDto);

            List<WidgetResponseDto> actual = widgetMapper.toWidgetsDtos(Collections.singletonList(widget));

            assertEquals(1L, actual.getFirst().getId());
            assertEquals("name", actual.getFirst().getName());
            assertEquals("description", actual.getFirst().getDescription());
            assertEquals("technicalName", actual.getFirst().getTechnicalName());
            assertEquals("info", actual.getFirst().getInfo());
            assertEquals(1L, actual.getFirst().getDelay());
            assertEquals("css", actual.getFirst().getCssContent());
            assertEquals(1L, actual.getFirst().getTimeout());
            assertEquals(WidgetAvailabilityEnum.ACTIVATED, actual.getFirst().getWidgetAvailability());
            assertEquals("encrypted", actual.getFirst().getImageToken());
            assertNull(actual.getFirst().getImage());
            assertNull(actual.getFirst().getLibraryTechnicalNames());
            assertEquals(categoryResponseDto, actual.getFirst().getCategory());
            assertEquals(1L, actual.getFirst().getRepositoryId());
            assertEquals(widgetParamResponseDto, actual.getFirst().getParams().getFirst());
        }
    }
}
