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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.export.ImportExportDto;
import com.michelin.suricate.model.dto.api.export.ImportExportProjectDto;
import com.michelin.suricate.model.dto.api.export.ImportExportRepositoryDto;
import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.model.entity.Repository;
import com.michelin.suricate.model.entity.Role;
import com.michelin.suricate.model.entity.User;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.service.api.ProjectService;
import com.michelin.suricate.service.api.RepositoryService;
import com.michelin.suricate.service.git.GitService;
import com.michelin.suricate.service.mapper.ProjectMapper;
import com.michelin.suricate.service.mapper.RepositoryMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ImportExportControllerTest {
    @Mock
    private RepositoryService repositoryService;

    @Mock
    private ProjectService projectService;

    @Mock
    private GitService gitService;

    @Mock
    private RepositoryMapper repositoryMapper;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ImportExportController importExportController;

    @Test
    void shouldExports() {
        Repository repository = new Repository();
        repository.setId(1L);

        ImportExportRepositoryDto importExportRepositoryDto = new ImportExportRepositoryDto();
        importExportRepositoryDto.setName("name");

        Project project = new Project();
        project.setId(1L);

        ImportExportProjectDto importExportProjectDto = new ImportExportProjectDto();
        importExportProjectDto.setName("name");

        when(repositoryService.getAll(any(), any()))
            .thenReturn(new PageImpl<>(Collections.singletonList(repository)));
        when(repositoryMapper.toImportExportRepositoryDto(any()))
            .thenReturn(importExportRepositoryDto);
        when(projectService.getAll(any(), any()))
            .thenReturn(new PageImpl<>(Collections.singletonList(project)));
        when(projectMapper.toImportExportProjectDto(any()))
            .thenReturn(importExportProjectDto);

        ResponseEntity<ImportExportDto> actual = importExportController.exports();

        assertEquals(MediaType.APPLICATION_JSON, actual.getHeaders().getContentType());
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertEquals(importExportRepositoryDto, actual.getBody().getRepositories().getFirst());
        assertEquals(importExportProjectDto, actual.getBody().getProjects().getFirst());
    }

    @Test
    void shouldImports() throws GitAPIException, IOException {
        ImportExportRepositoryDto importExportRepositoryDto = new ImportExportRepositoryDto();
        importExportRepositoryDto.setName("name");

        ImportExportProjectDto importExportProjectDto = new ImportExportProjectDto();
        importExportProjectDto.setName("name");

        ImportExportDto importExportDto = new ImportExportDto();
        importExportDto.setRepositories(Collections.singletonList(importExportRepositoryDto));
        importExportDto.setProjects(Collections.singletonList(importExportProjectDto));

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        Repository repository = new Repository();
        repository.setName("name");

        Project project = new Project();

        when(repositoryMapper.toRepositoryEntity(any()))
            .thenReturn(repository);
        when(repositoryService.findByName(any()))
            .thenReturn(Optional.of(repository));
        when(projectMapper.toProjectEntity(any(ImportExportProjectDto.class)))
            .thenReturn(project);
        when(projectService.createUpdateProjects(any(), any()))
            .thenReturn(Collections.emptyList());

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        ResponseEntity<Void> actual = importExportController.imports(localUser, importExportDto);

        assertEquals(MediaType.APPLICATION_JSON, actual.getHeaders().getContentType());
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNull(actual.getBody());

        verify(repositoryMapper)
            .toRepositoryEntity(importExportRepositoryDto);
        verify(repositoryService)
            .findByName("name");
        verify(repositoryService)
            .addOrUpdateRepositories(argThat(repositories -> repositories.contains(repository)));
        verify(projectMapper)
            .toProjectEntity(importExportProjectDto);
        verify(projectService)
            .createUpdateProjects(argThat(projects -> projects.contains(project)),
                eq(user));
    }
}
