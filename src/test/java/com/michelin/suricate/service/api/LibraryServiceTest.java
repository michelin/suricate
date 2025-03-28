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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entity.Asset;
import com.michelin.suricate.model.entity.Library;
import com.michelin.suricate.model.entity.Library_;
import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.model.entity.ProjectGrid;
import com.michelin.suricate.model.entity.ProjectWidget;
import com.michelin.suricate.model.entity.Widget;
import com.michelin.suricate.repository.LibraryRepository;
import com.michelin.suricate.service.specification.LibrarySearchSpecification;
import com.michelin.suricate.util.IdUtils;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {
    @Mock
    private LibraryRepository libraryRepository;

    @Mock
    private AssetService assetService;

    @Mock
    private SingularAttribute<Library, String> technicalName;

    @InjectMocks
    private LibraryService libraryService;

    @Test
    void shouldGetAll() {
        Library library = new Library();
        library.setId(1L);

        Library_.technicalName = technicalName;
        when(libraryRepository.findAll(any(LibrarySearchSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(library)));

        Page<Library> actual = libraryService.getAll("search", Pageable.unpaged());

        assertFalse(actual.isEmpty());
        assertTrue(actual.getContent().contains(library));

        verify(libraryRepository)
                .findAll(
                        Mockito.<LibrarySearchSpecification>argThat(
                                specification -> specification.getSearch().equals("search")
                                        && specification.getAttributes().contains(technicalName.getName())),
                        Mockito.<Pageable>argThat(pageable -> pageable.equals(Pageable.unpaged())));
    }

    @Test
    void shouldGetLibrariesByProjectWhenNoWidget() {
        Project project = new Project();
        project.setGrids(Collections.singleton(new ProjectGrid()));

        List<Library> actual = libraryService.getLibrariesByProject(project);

        assertTrue(actual.isEmpty());

        verify(libraryRepository, never()).findDistinctByWidgetsIdIn(any());
    }

    @Test
    void shouldGetLibrariesByProject() {
        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setWidget(widget);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setWidgets(Collections.singleton(projectWidget));

        Project project = new Project();
        project.setGrids(Collections.singleton(projectGrid));

        Library library = new Library();

        when(libraryRepository.findDistinctByWidgetsIdIn(any())).thenReturn(Collections.singletonList(library));

        List<Library> actual = libraryService.getLibrariesByProject(project);

        assertFalse(actual.isEmpty());
        assertTrue(actual.contains(library));

        verify(libraryRepository).findDistinctByWidgetsIdIn(Collections.singletonList(1L));
    }

    @Test
    void shouldGetLibraryTokensByProject() {
        try (MockedStatic<IdUtils> mocked = mockStatic(IdUtils.class)) {
            Widget widget = new Widget();
            widget.setId(1L);

            ProjectWidget projectWidget = new ProjectWidget();
            projectWidget.setWidget(widget);

            ProjectGrid projectGrid = new ProjectGrid();
            projectGrid.setWidgets(Collections.singleton(projectWidget));

            Project project = new Project();
            project.setGrids(Collections.singleton(projectGrid));

            Asset asset = new Asset();
            asset.setId(1L);

            Library library = new Library();
            library.setAsset(asset);

            mocked.when(() -> IdUtils.encrypt(1L)).thenReturn("token");
            when(libraryRepository.findDistinctByWidgetsIdIn(any())).thenReturn(Collections.singletonList(library));

            List<String> actual = libraryService.getLibraryTokensByProject(project);

            assertFalse(actual.isEmpty());
            assertTrue(actual.contains("token"));

            verify(libraryRepository).findDistinctByWidgetsIdIn(Collections.singletonList(1L));
        }
    }

    @Test
    void shouldCreateUpdateLibrariesWhenNull() {
        List<Library> actual = libraryService.createUpdateLibraries(null);

        assertTrue(actual.isEmpty());

        verify(libraryRepository, never()).findByTechnicalName(any());
        verify(assetService, never()).save(any());
        verify(libraryRepository, never()).saveAll(any());
        verify(libraryRepository, never()).findAll();
    }

    @Test
    void shouldCreateLibraries() {
        Asset asset = new Asset();
        asset.setId(1L);

        Library library = new Library();
        library.setAsset(asset);
        library.setTechnicalName("technicalName");

        when(libraryRepository.findByTechnicalName(any())).thenReturn(null);
        when(assetService.save(any())).thenAnswer(answer -> answer.getArgument(0));
        when(libraryRepository.saveAll(any())).thenAnswer(answer -> answer.getArgument(0));
        when(libraryRepository.findAll()).thenReturn(Collections.singletonList(library));

        List<Library> actual = libraryService.createUpdateLibraries(Collections.singletonList(library));

        assertFalse(actual.isEmpty());
        assertTrue(actual.contains(library));

        verify(libraryRepository).findByTechnicalName("technicalName");
        verify(assetService).save(asset);
        verify(libraryRepository).saveAll(Collections.singletonList(library));
        verify(libraryRepository).findAll();
    }

    @Test
    void shouldCreateLibrariesNoAsset() {
        Library library = new Library();
        library.setTechnicalName("technicalName");

        when(libraryRepository.findByTechnicalName(any())).thenReturn(null);
        when(libraryRepository.saveAll(any())).thenAnswer(answer -> answer.getArgument(0));
        when(libraryRepository.findAll()).thenReturn(Collections.singletonList(library));

        List<Library> actual = libraryService.createUpdateLibraries(Collections.singletonList(library));

        assertFalse(actual.isEmpty());
        assertTrue(actual.contains(library));

        verify(libraryRepository).findByTechnicalName("technicalName");
        verify(assetService, never()).save(any());
        verify(libraryRepository).saveAll(Collections.singletonList(library));
        verify(libraryRepository).findAll();
    }

    @Test
    void shouldUpdateLibraries() {
        Asset oldAsset = new Asset();
        oldAsset.setId(2L);

        Library oldLibrary = new Library();
        oldLibrary.setId(2L);
        oldLibrary.setAsset(oldAsset);

        Library library = new Library();
        library.setAsset(new Asset());
        library.setTechnicalName("technicalName");

        when(libraryRepository.findByTechnicalName(any())).thenReturn(oldLibrary);
        when(assetService.save(any())).thenAnswer(answer -> answer.getArgument(0));
        when(libraryRepository.saveAll(any())).thenAnswer(answer -> answer.getArgument(0));
        when(libraryRepository.findAll()).thenReturn(Collections.singletonList(library));

        List<Library> actual = libraryService.createUpdateLibraries(Collections.singletonList(library));

        assertFalse(actual.isEmpty());
        assertEquals(library, actual.getFirst());
        assertEquals(2L, actual.getFirst().getId());
        assertEquals(2L, actual.getFirst().getAsset().getId());

        verify(libraryRepository).findByTechnicalName("technicalName");
        verify(assetService).save(argThat(createdAsset -> createdAsset.getId().equals(2L)));
        verify(libraryRepository).saveAll(Collections.singletonList(library));
        verify(libraryRepository).findAll();
    }

    @Test
    void shouldUpdateLibrariesNoAssetBefore() {
        Library oldLibrary = new Library();
        oldLibrary.setId(2L);

        Library library = new Library();
        library.setAsset(new Asset());
        library.setTechnicalName("technicalName");

        when(libraryRepository.findByTechnicalName(any())).thenReturn(oldLibrary);
        when(assetService.save(any())).thenAnswer(answer -> answer.getArgument(0));
        when(libraryRepository.saveAll(any())).thenAnswer(answer -> answer.getArgument(0));
        when(libraryRepository.findAll()).thenReturn(Collections.singletonList(library));

        List<Library> actual = libraryService.createUpdateLibraries(Collections.singletonList(library));

        assertFalse(actual.isEmpty());
        assertEquals(library, actual.getFirst());
        assertEquals(2L, actual.getFirst().getId());
        assertNotNull(actual.getFirst().getAsset());

        verify(libraryRepository).findByTechnicalName("technicalName");
        verify(assetService).save(argThat(createdAsset -> createdAsset.getId() == null));
        verify(libraryRepository).saveAll(Collections.singletonList(library));
        verify(libraryRepository).findAll();
    }
}
