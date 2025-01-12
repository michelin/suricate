/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.michelin.suricate.service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.widget.WidgetRequestDto;
import com.michelin.suricate.model.dto.js.WidgetVariableResponseDto;
import com.michelin.suricate.model.entity.Asset;
import com.michelin.suricate.model.entity.Category;
import com.michelin.suricate.model.entity.Library;
import com.michelin.suricate.model.entity.Repository;
import com.michelin.suricate.model.entity.Widget;
import com.michelin.suricate.model.entity.WidgetParam;
import com.michelin.suricate.model.entity.WidgetParamValue;
import com.michelin.suricate.model.enumeration.DataTypeEnum;
import com.michelin.suricate.model.enumeration.RepositoryTypeEnum;
import com.michelin.suricate.model.enumeration.WidgetAvailabilityEnum;
import com.michelin.suricate.repository.WidgetParamRepository;
import com.michelin.suricate.repository.WidgetRepository;
import com.michelin.suricate.service.specification.WidgetSearchSpecification;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
class WidgetServiceTest {
    @Mock
    private AssetService assetService;

    @Mock
    private WidgetRepository widgetRepository;

    @Mock
    private WidgetParamRepository widgetParamRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private WidgetService widgetService;

    @Test
    void shouldFindOne() {
        Widget widget = new Widget();
        widget.setId(1L);

        when(widgetRepository.findById(any()))
            .thenReturn(Optional.of(widget));

        Optional<Widget> actual = widgetService.findOne(1L);

        assertTrue(actual.isPresent());
        assertEquals(widget, actual.get());

        verify(widgetRepository)
            .findById(1L);
    }


    @Test
    void shouldFindOneByTechnicalName() {
        Widget widget = new Widget();
        widget.setId(1L);

        when(widgetRepository.findByTechnicalName(any()))
            .thenReturn(Optional.of(widget));

        Optional<Widget> actual = widgetService.findOneByTechnicalName("technicalName");

        assertTrue(actual.isPresent());
        assertEquals(widget, actual.get());

        verify(widgetRepository)
            .findByTechnicalName("technicalName");
    }

    @Test
    void shouldGetAll() {
        Widget widget = new Widget();
        widget.setId(1L);

        when(widgetRepository.findAll(any(WidgetSearchSpecification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(widget)));

        Page<Widget> actual = widgetService.getAll("search", Pageable.unpaged());

        assertFalse(actual.isEmpty());
        assertEquals(widget, actual.get().toList().getFirst());

        verify(widgetRepository)
            .findAll(Mockito.<WidgetSearchSpecification>argThat(
                    specification -> specification.getSearch().equals("search")
                        && specification.getAttributes().isEmpty()),
                Mockito.<Pageable>argThat(pageable -> pageable.equals(Pageable.unpaged())));
    }

    @Test
    void shouldGetWidgetsByCategory() {
        Widget widget = new Widget();
        widget.setId(1L);
        List<Widget> widgets = Collections.singletonList(widget);

        when(widgetRepository.findAllByCategoryIdOrderByNameAsc(any()))
            .thenReturn(widgets);

        Optional<List<Widget>> actual = widgetService.getWidgetsByCategory(1L);

        assertTrue(actual.isPresent());
        assertTrue(actual.get().contains(widget));

        verify(widgetRepository)
            .findAllByCategoryIdOrderByNameAsc(1L);
    }

    @Test
    void shouldGetWidgetsByCategoryEmptyList() {
        when(widgetRepository.findAllByCategoryIdOrderByNameAsc(any()))
            .thenReturn(Collections.emptyList());

        Optional<List<Widget>> actual = widgetService.getWidgetsByCategory(1L);

        assertTrue(actual.isEmpty());

        verify(widgetRepository)
            .findAllByCategoryIdOrderByNameAsc(1L);
    }

    @Test
    void shouldGetWidgetsByCategoryNull() {
        when(widgetRepository.findAllByCategoryIdOrderByNameAsc(any()))
            .thenReturn(null);

        Optional<List<Widget>> actual = widgetService.getWidgetsByCategory(1L);

        assertTrue(actual.isEmpty());

        verify(widgetRepository)
            .findAllByCategoryIdOrderByNameAsc(1L);
    }

    @Test
    void shouldGetWidgetParametersWithCategoryParameters() {
        WidgetParam widgetParam = new WidgetParam();
        widgetParam.setId(1L);

        WidgetParam widgetParamTwo = new WidgetParam();
        widgetParam.setId(2L);

        Widget widget = new Widget();
        widget.setId(1L);
        widget.setWidgetParams(Collections.singletonList(widgetParamTwo));

        List<WidgetParam> widgetParams = Collections.singletonList(widgetParam);

        when(categoryService.getCategoryParametersByWidget(any()))
            .thenReturn(widgetParams);

        List<WidgetParam> actual = widgetService.getWidgetParametersWithCategoryParameters(widget);

        assertEquals(2, actual.size());
        assertTrue(actual.containsAll(List.of(widgetParam, widgetParamTwo)));

        verify(categoryService)
            .getCategoryParametersByWidget(widget);
    }

    @Test
    void shouldGetWidgetParametersForJsExecution() {
        WidgetParam widgetParam = new WidgetParam();
        widgetParam.setId(1L);
        widgetParam.setName("Name1");
        widgetParam.setDescription("Description");
        widgetParam.setType(DataTypeEnum.TEXT);
        widgetParam.setDefaultValue("defaultValue");

        WidgetParamValue widgetParamValue = new WidgetParamValue();
        widgetParamValue.setId(1L);
        widgetParamValue.setJsKey("key");
        widgetParamValue.setValue("value");

        WidgetParam widgetParamTwo = new WidgetParam();
        widgetParamTwo.setId(2L);
        widgetParamTwo.setName("Name2");
        widgetParamTwo.setDescription("Description");
        widgetParamTwo.setType(DataTypeEnum.COMBO);
        widgetParamTwo.setPossibleValuesMap(Collections.singletonList(widgetParamValue));

        WidgetParam widgetParamThree = new WidgetParam();
        widgetParamThree.setId(3L);
        widgetParamThree.setName("Name3");
        widgetParamThree.setDescription("Description");
        widgetParamThree.setType(DataTypeEnum.MULTIPLE);
        widgetParamThree.setPossibleValuesMap(Collections.singletonList(widgetParamValue));

        WidgetParam widgetParamFour = new WidgetParam();
        widgetParamFour.setId(4L);
        widgetParamFour.setName("Name4");
        widgetParamFour.setDescription("Description");
        widgetParamFour.setType(DataTypeEnum.TEXT);
        widgetParamFour.setPossibleValuesMap(Collections.singletonList(widgetParamValue));

        Widget widget = new Widget();
        widget.setId(1L);

        when(categoryService.getCategoryParametersByWidget(any()))
            .thenReturn(Arrays.asList(widgetParam, widgetParamTwo, widgetParamThree, widgetParamFour));

        List<WidgetVariableResponseDto> actual = widgetService.getWidgetParametersForJsExecution(widget);

        assertEquals(4, actual.size());

        verify(categoryService)
            .getCategoryParametersByWidget(widget);
    }

    @Test
    void shouldUpdateWidget() {
        Widget widget = new Widget();
        widget.setId(1L);

        WidgetRequestDto widgetRequestDto = new WidgetRequestDto();
        widgetRequestDto.setWidgetAvailability(WidgetAvailabilityEnum.ACTIVATED);

        when(widgetRepository.findById(any()))
            .thenReturn(Optional.of(widget));
        when(widgetRepository.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));

        Optional<Widget> actual = widgetService.updateWidget(1L, widgetRequestDto);

        assertTrue(actual.isPresent());
        assertEquals(widget, actual.get());

        verify(widgetRepository)
            .findById(1L);
        verify(widgetRepository)
            .save(widget);
    }

    @Test
    void shouldNotUpdateWidgetWhenNotExist() {
        WidgetRequestDto widgetRequestDto = new WidgetRequestDto();
        widgetRequestDto.setWidgetAvailability(WidgetAvailabilityEnum.ACTIVATED);

        when(widgetRepository.findById(any()))
            .thenReturn(Optional.empty());

        Optional<Widget> actual = widgetService.updateWidget(1L, widgetRequestDto);

        assertTrue(actual.isEmpty());

        verify(widgetRepository)
            .findById(1L);
    }

    @Test
    void shouldAddOrUpdateWidgetsNoWidget() {
        Repository repository = new Repository();
        repository.setId(1L);
        repository.setName("repository");
        repository.setBranch("master");

        Library library = new Library();
        library.setId(1L);

        Category category = new Category();
        category.setId(1L);

        widgetService.addOrUpdateWidgets(category, Collections.singletonList(library), repository);

        verify(widgetRepository, never())
            .findByTechnicalName(any());
        verify(assetService, never())
            .save(any());
        verify(widgetParamRepository, never())
            .deleteById(any());
        verify(widgetRepository, never())
            .save(any());
    }

    @Test
    void shouldAddOrUpdateWidgets() {
        Repository repository = new Repository();
        repository.setId(1L);
        repository.setName("repository");
        repository.setLocalPath("/tmp");
        repository.setType(RepositoryTypeEnum.LOCAL);

        Asset asset = new Asset();
        asset.setId(1L);
        asset.setContent(new byte[1]);

        Library library = new Library();
        library.setId(1L);
        library.setTechnicalName("technicalName");
        library.setAsset(asset);

        WidgetParamValue currentWidgetParamValue = new WidgetParamValue();
        currentWidgetParamValue.setId(12L);
        currentWidgetParamValue.setJsKey("widgetParamKey");

        WidgetParam currentWidgetParam = new WidgetParam();
        currentWidgetParam.setId(11L);
        currentWidgetParam.setName("widgetParam");
        currentWidgetParam.setPossibleValuesMap(Collections.singletonList(currentWidgetParamValue));

        WidgetParam currentWidgetParamNotPresentAnymore = new WidgetParam();
        currentWidgetParamNotPresentAnymore.setId(13L);

        Asset currentWidgetImage = new Asset();
        currentWidgetImage.setId(10L);

        Widget currentWidget = new Widget();
        currentWidget.setId(1L);
        currentWidget.setWidgetAvailability(WidgetAvailabilityEnum.ACTIVATED);
        currentWidget.setImage(currentWidgetImage);
        currentWidget.setWidgetParams(Arrays.asList(currentWidgetParam, currentWidgetParamNotPresentAnymore));

        Library widgetLibrary = new Library();
        widgetLibrary.setTechnicalName("technicalName");

        WidgetParamValue widgetParamValue = new WidgetParamValue();
        widgetParamValue.setJsKey("widgetParamKey");

        WidgetParam widgetParam = new WidgetParam();
        widgetParam.setName("widgetParam");
        widgetParam.setPossibleValuesMap(Collections.singletonList(widgetParamValue));

        Asset widgetImage = new Asset();

        Widget widget = new Widget();
        widget.setTechnicalName("widgetTechnicalName");
        widget.setLibraries(Collections.singleton(widgetLibrary));
        widget.setImage(widgetImage);
        widget.setWidgetParams(Collections.singletonList(widgetParam));

        Category category = new Category();
        category.setId(1L);
        category.setWidgets(Collections.singleton(widget));

        when(widgetRepository.findByTechnicalName(any()))
            .thenReturn(Optional.of(currentWidget));
        when(assetService.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));

        widgetService.addOrUpdateWidgets(category, Collections.singletonList(library), repository);

        assertEquals(1L, widget.getId());
        assertEquals(WidgetAvailabilityEnum.ACTIVATED, widget.getWidgetAvailability());
        assertEquals(category, widget.getCategory());
        assertEquals(repository, widget.getRepository());
        assertTrue(widget.getLibraries().contains(library));
        assertEquals(10L, widget.getImage().getId());
        assertEquals(11L, new ArrayList<>(widget.getWidgetParams()).getFirst().getId());
        assertEquals(
            12L,
            new ArrayList<>(new ArrayList<>(widget.getWidgetParams())
                .getFirst().getPossibleValuesMap())
                .getFirst().getId()
        );

        verify(widgetRepository)
            .findByTechnicalName("widgetTechnicalName");
        verify(assetService)
            .save(widgetImage);
        verify(widgetParamRepository)
            .deleteAllById(List.of(13L));
        verify(widgetRepository)
            .save(widget);
    }

    @Test
    void shouldNotDeleteWidgetParamWhenNameDoesNotChange() {
        Repository repository = new Repository();
        repository.setId(1L);
        repository.setName("repository");
        repository.setBranch("master");
        repository.setType(RepositoryTypeEnum.REMOTE);

        Asset asset = new Asset();
        asset.setId(1L);
        asset.setContent(new byte[1]);

        Library library = new Library();
        library.setId(1L);
        library.setTechnicalName("technicalName");
        library.setAsset(asset);

        WidgetParamValue currentWidgetParamValue = new WidgetParamValue();
        currentWidgetParamValue.setId(12L);
        currentWidgetParamValue.setJsKey("widgetParamKey");

        WidgetParam currentWidgetParam = new WidgetParam();
        currentWidgetParam.setId(13L);
        currentWidgetParam.setName("widgetParam");
        currentWidgetParam.setDescription("description");

        Asset currentWidgetImage = new Asset();
        currentWidgetImage.setId(10L);

        Widget currentWidget = new Widget();
        currentWidget.setId(1L);
        currentWidget.setWidgetAvailability(WidgetAvailabilityEnum.ACTIVATED);
        currentWidget.setImage(currentWidgetImage);
        currentWidget.setWidgetParams(List.of(currentWidgetParam));

        Library widgetLibrary = new Library();
        widgetLibrary.setTechnicalName("technicalName");

        WidgetParamValue widgetParamValue = new WidgetParamValue();
        widgetParamValue.setJsKey("widgetParamKey");

        WidgetParam widgetParam = new WidgetParam();
        widgetParam.setName("widgetParam");
        // Only the description has changed. This will not trigger the deletion of the widget param
        currentWidgetParam.setDescription("descriptionUpdated");

        Asset widgetImage = new Asset();

        Widget widget = new Widget();
        widget.setTechnicalName("widgetTechnicalName");
        widget.setLibraries(Collections.singleton(widgetLibrary));
        widget.setImage(widgetImage);
        widget.setWidgetParams(Collections.singletonList(widgetParam));

        Category category = new Category();
        category.setId(1L);
        category.setWidgets(Collections.singleton(widget));

        when(widgetRepository.findByTechnicalName(any()))
            .thenReturn(Optional.of(currentWidget));
        when(assetService.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));

        widgetService.addOrUpdateWidgets(category, Collections.singletonList(library), repository);

        assertEquals(1L, widget.getId());
        assertEquals(WidgetAvailabilityEnum.ACTIVATED, widget.getWidgetAvailability());
        assertEquals(category, widget.getCategory());
        assertEquals(repository, widget.getRepository());
        assertTrue(widget.getLibraries().contains(library));
        assertEquals(10L, widget.getImage().getId());
        assertEquals(13L, new ArrayList<>(widget.getWidgetParams()).getFirst().getId());

        verify(widgetRepository)
            .findByTechnicalName("widgetTechnicalName");
        verify(assetService)
            .save(widgetImage);
        verify(widgetParamRepository, never())
            .deleteAllById(List.of(13L));
        verify(widgetRepository)
            .save(widget);
    }

    @Test
    void shouldAddOrUpdateWidgetsNoAvailability() {
        Repository repository = new Repository();
        repository.setId(1L);
        repository.setName("repository");
        repository.setBranch("master");
        repository.setType(RepositoryTypeEnum.REMOTE);

        Asset asset = new Asset();
        asset.setId(1L);
        asset.setContent(new byte[1]);

        Library library = new Library();
        library.setId(1L);
        library.setTechnicalName("technicalName");
        library.setAsset(asset);

        WidgetParamValue currentWidgetParamValue = new WidgetParamValue();
        currentWidgetParamValue.setId(12L);
        currentWidgetParamValue.setJsKey("widgetParamKey");

        WidgetParam currentWidgetParam = new WidgetParam();
        currentWidgetParam.setId(11L);
        currentWidgetParam.setName("widgetParam");
        currentWidgetParam.setPossibleValuesMap(Collections.singletonList(currentWidgetParamValue));

        WidgetParam currentWidgetParamNotPresentAnymore = new WidgetParam();
        currentWidgetParamNotPresentAnymore.setId(13L);

        Asset currentWidgetImage = new Asset();
        currentWidgetImage.setId(10L);

        Widget currentWidget = new Widget();
        currentWidget.setId(1L);
        currentWidget.setImage(currentWidgetImage);
        currentWidget.setWidgetParams(Arrays.asList(currentWidgetParam, currentWidgetParamNotPresentAnymore));

        Library widgetLibrary = new Library();
        widgetLibrary.setTechnicalName("technicalName");

        WidgetParamValue widgetParamValue = new WidgetParamValue();
        widgetParamValue.setJsKey("widgetParamKey");

        WidgetParam widgetParam = new WidgetParam();
        widgetParam.setName("widgetParam");
        widgetParam.setPossibleValuesMap(Collections.singletonList(widgetParamValue));

        Asset widgetImage = new Asset();

        Widget widget = new Widget();
        widget.setTechnicalName("widgetTechnicalName");
        widget.setLibraries(Collections.singleton(widgetLibrary));
        widget.setImage(widgetImage);
        widget.setWidgetParams(Collections.singletonList(widgetParam));

        Category category = new Category();
        category.setId(1L);
        category.setWidgets(Collections.singleton(widget));

        when(widgetRepository.findByTechnicalName(any()))
            .thenReturn(Optional.of(currentWidget));
        when(assetService.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));

        widgetService.addOrUpdateWidgets(category, Collections.singletonList(library), repository);

        assertEquals(1L, widget.getId());
        assertEquals(WidgetAvailabilityEnum.ACTIVATED, widget.getWidgetAvailability());
        assertEquals(category, widget.getCategory());
        assertEquals(repository, widget.getRepository());
        assertTrue(widget.getLibraries().contains(library));
        assertEquals(10L, widget.getImage().getId());
        assertEquals(11L, new ArrayList<>(widget.getWidgetParams()).getFirst().getId());
        assertEquals(
            12L,
            new ArrayList<>(new ArrayList<>(widget.getWidgetParams())
                .getFirst()
                .getPossibleValuesMap())
                .getFirst().getId()
        );

        verify(widgetRepository)
            .findByTechnicalName("widgetTechnicalName");
        verify(assetService)
            .save(widgetImage);
        verify(widgetParamRepository)
            .deleteAllById(List.of(13L));
        verify(widgetRepository)
            .save(widget);
    }

    @Test
    void shouldGetWidgetParamValuesAsMap() {
        WidgetParamValue widgetParamValue = new WidgetParamValue();
        widgetParamValue.setId(1L);
        widgetParamValue.setJsKey("key");
        widgetParamValue.setValue("value");

        Map<String, String> actual = widgetService.getWidgetParamValuesAsMap(Collections.singleton(widgetParamValue));

        assertEquals("value", actual.get("key"));
    }
}
