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
package com.michelin.suricate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.repository.RepositoryRequestDto;
import com.michelin.suricate.model.dto.api.repository.RepositoryResponseDto;
import com.michelin.suricate.model.dto.api.widget.WidgetResponseDto;
import com.michelin.suricate.model.entity.Repository;
import com.michelin.suricate.service.api.RepositoryService;
import com.michelin.suricate.service.git.GitService;
import com.michelin.suricate.service.mapper.RepositoryMapper;
import com.michelin.suricate.service.mapper.WidgetMapper;
import com.michelin.suricate.util.exception.ObjectNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class RepositoryControllerTest {
    @Mock
    private RepositoryService repositoryService;

    @Mock
    private GitService gitService;

    @Mock
    private RepositoryMapper repositoryMapper;

    @Mock
    private WidgetMapper widgetMapper;

    @InjectMocks
    private RepositoryController repositoryController;

    @Test
    void shouldGetAll() {
        RepositoryResponseDto repositoryResponseDto = new RepositoryResponseDto();
        repositoryResponseDto.setId(1L);

        Repository repository = new Repository();
        repository.setId(1L);

        when(repositoryService.getAll(any(), any())).thenReturn(new PageImpl<>(Collections.singletonList(repository)));
        when(repositoryMapper.toRepositoryDtoNoWidgets(any())).thenReturn(repositoryResponseDto);

        Page<RepositoryResponseDto> actual = repositoryController.getAll("search", Pageable.unpaged());

        assertFalse(actual.isEmpty());
        assertEquals(1, actual.get().count());
        assertEquals(repositoryResponseDto, actual.get().toList().getFirst());
    }

    @Test
    void shouldCreateRepositoryEnabled() throws GitAPIException, IOException {
        RepositoryResponseDto repositoryResponseDto = new RepositoryResponseDto();
        repositoryResponseDto.setId(1L);

        RepositoryRequestDto repositoryRequestDto = new RepositoryRequestDto();
        repositoryRequestDto.setName("name");

        Repository repository = new Repository();
        repository.setId(1L);
        repository.setEnabled(true);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        when(repositoryMapper.toRepositoryEntity(any(), any())).thenReturn(repository);
        when(repositoryMapper.toRepositoryDtoNoWidgets(any())).thenReturn(repositoryResponseDto);

        ResponseEntity<RepositoryResponseDto> actual = repositoryController.createOne(repositoryRequestDto);

        assertEquals(HttpStatus.CREATED, actual.getStatusCode());
        assertEquals(repositoryResponseDto, actual.getBody());

        verify(gitService).updateWidgetFromEnabledGitRepositories();
    }

    @Test
    void shouldCreateRepositoryDisabled() throws GitAPIException, IOException {
        RepositoryResponseDto repositoryResponseDto = new RepositoryResponseDto();
        repositoryResponseDto.setId(1L);

        RepositoryRequestDto repositoryRequestDto = new RepositoryRequestDto();
        repositoryRequestDto.setName("name");

        Repository repository = new Repository();
        repository.setId(1L);
        repository.setEnabled(false);

        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
        when(repositoryMapper.toRepositoryEntity(any(), any())).thenReturn(repository);
        when(repositoryMapper.toRepositoryDtoNoWidgets(any())).thenReturn(repositoryResponseDto);

        ResponseEntity<RepositoryResponseDto> actual = repositoryController.createOne(repositoryRequestDto);

        assertEquals(HttpStatus.CREATED, actual.getStatusCode());
        assertEquals(repositoryResponseDto, actual.getBody());

        verify(gitService, never()).updateWidgetFromEnabledGitRepositories();
    }

    @Test
    void shouldGetOneByIdNotFound() {
        RepositoryResponseDto repositoryResponseDto = new RepositoryResponseDto();
        repositoryResponseDto.setId(1L);

        Repository repository = new Repository();
        repository.setId(1L);

        when(repositoryService.getOneById(any())).thenReturn(Optional.empty());

        ObjectNotFoundException exception =
                assertThrows(ObjectNotFoundException.class, () -> repositoryController.getOneById(1L));

        assertEquals("Repository '1' not found", exception.getMessage());
    }

    @Test
    void shouldGetOneById() {
        RepositoryResponseDto repositoryResponseDto = new RepositoryResponseDto();
        repositoryResponseDto.setId(1L);

        Repository repository = new Repository();
        repository.setId(1L);

        when(repositoryService.getOneById(any())).thenReturn(Optional.of(repository));
        when(repositoryMapper.toRepositoryDtoNoWidgets(any())).thenReturn(repositoryResponseDto);

        ResponseEntity<RepositoryResponseDto> actual = repositoryController.getOneById(1L);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals(repositoryResponseDto, actual.getBody());
    }

    @Test
    void shouldUpdateOneByIdNotFound() {
        RepositoryRequestDto repositoryRequestDto = new RepositoryRequestDto();
        repositoryRequestDto.setName("name");

        when(repositoryService.existsById(any())).thenReturn(false);

        ObjectNotFoundException exception = assertThrows(
                ObjectNotFoundException.class,
                () -> repositoryController.updateOneById(1L, repositoryRequestDto, true));

        assertEquals("Repository '1' not found", exception.getMessage());
    }

    @Test
    void shouldUpdateOneByIdSyncDisabled() throws GitAPIException, IOException {
        RepositoryRequestDto repositoryRequestDto = new RepositoryRequestDto();
        repositoryRequestDto.setName("name");

        Repository repository = new Repository();
        repository.setId(1L);

        when(repositoryService.existsById(any())).thenReturn(true);
        when(repositoryMapper.toRepositoryEntity(any(), any())).thenReturn(repository);

        ResponseEntity<Void> actual = repositoryController.updateOneById(1L, repositoryRequestDto, true);

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
    }

    @Test
    void shouldUpdateOneByIdSyncDisabledOnRepo() throws GitAPIException, IOException {
        RepositoryRequestDto repositoryRequestDto = new RepositoryRequestDto();
        repositoryRequestDto.setName("name");

        Repository repository = new Repository();
        repository.setId(1L);
        repository.setEnabled(false);

        when(repositoryService.existsById(any())).thenReturn(true);
        when(repositoryMapper.toRepositoryEntity(any(), any())).thenReturn(repository);

        ResponseEntity<Void> actual = repositoryController.updateOneById(1L, repositoryRequestDto, false);

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
    }

    @Test
    void shouldUpdateOneByIdSyncEnabled() throws GitAPIException, IOException {
        RepositoryRequestDto repositoryRequestDto = new RepositoryRequestDto();
        repositoryRequestDto.setName("name");

        Repository repository = new Repository();
        repository.setId(1L);
        repository.setEnabled(true);

        when(repositoryService.existsById(any())).thenReturn(true);
        when(repositoryMapper.toRepositoryEntity(any(), any())).thenReturn(repository);

        ResponseEntity<Void> actual = repositoryController.updateOneById(1L, repositoryRequestDto, false);

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
    }

    @Test
    void shouldSynchronize() throws GitAPIException, IOException {
        ResponseEntity<Void> actual = repositoryController.synchronize();

        assertEquals(HttpStatus.NO_CONTENT, actual.getStatusCode());
    }

    @Test
    void shouldGetRepositoryWidgetNotFound() {
        when(repositoryService.getOneById(any())).thenReturn(Optional.empty());

        ObjectNotFoundException exception =
                assertThrows(ObjectNotFoundException.class, () -> repositoryController.getRepositoryWidget(1L));

        assertEquals("Repository '1' not found", exception.getMessage());
    }

    @Test
    void shouldGetRepositoryWidget() {
        Repository repository = new Repository();
        repository.setId(1L);

        WidgetResponseDto widgetResponseDto = new WidgetResponseDto();
        widgetResponseDto.setId(1L);

        when(repositoryService.getOneById(any())).thenReturn(Optional.of(repository));
        when(widgetMapper.toWidgetsDtos(any())).thenReturn(Collections.singletonList(widgetResponseDto));

        ResponseEntity<List<WidgetResponseDto>> actual = repositoryController.getRepositoryWidget(1L);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertTrue(actual.getBody().contains(widgetResponseDto));
    }
}
