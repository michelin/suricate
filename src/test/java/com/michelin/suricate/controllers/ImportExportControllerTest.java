package com.michelin.suricate.controllers;

import com.michelin.suricate.model.dto.api.export.ImportExportDto;
import com.michelin.suricate.model.dto.api.export.ImportExportProjectDto;
import com.michelin.suricate.model.dto.api.export.ImportExportRepositoryDto;
import com.michelin.suricate.model.entities.Project;
import com.michelin.suricate.model.entities.Repository;
import com.michelin.suricate.model.entities.Role;
import com.michelin.suricate.model.entities.User;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.services.api.ProjectService;
import com.michelin.suricate.services.api.RepositoryService;
import com.michelin.suricate.services.git.GitService;
import com.michelin.suricate.services.mapper.ProjectMapper;
import com.michelin.suricate.services.mapper.RepositoryMapper;
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

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        when(repositoryMapper.toImportExportRepositoryDTO(any()))
                .thenReturn(importExportRepositoryDto);
        when(projectService.getAll(any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(project)));
        when(projectMapper.toImportExportProjectDTO(any()))
                .thenReturn(importExportProjectDto);

        ResponseEntity<ImportExportDto> actual = importExportController.exports();

        assertThat(actual.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody().getRepositories().get(0)).isEqualTo(importExportRepositoryDto);
        assertThat(actual.getBody().getProjects().get(0)).isEqualTo(importExportProjectDto);
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

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        Repository repository = new Repository();
        repository.setName("name");

        Project project = new Project();

        when(repositoryMapper.toRepositoryEntity(any()))
                .thenReturn(repository);
        when(repositoryService.findByName(any()))
                .thenReturn(Optional.of(repository));
        doNothing().when(repositoryService)
                .addOrUpdateRepositories(any());
        doNothing().when(gitService)
                .updateWidgetFromEnabledGitRepositories();
        when(projectMapper.toProjectEntity(any(ImportExportProjectDto.class)))
                .thenReturn(project);
        when(projectService.createUpdateProjects(any(), any()))
                .thenReturn(Collections.emptyList());

        ResponseEntity<Void> actual = importExportController.imports(localUser, importExportDto);

        assertThat(actual.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNull();

        verify(repositoryMapper, times(1))
                .toRepositoryEntity(importExportRepositoryDto);
        verify(repositoryService, times(1))
                .findByName("name");
        verify(repositoryService, times(1))
                .addOrUpdateRepositories(argThat(repositories -> repositories.contains(repository)));
        verify(projectMapper, times(1))
                .toProjectEntity(importExportProjectDto);
        verify(projectService, times(1))
                .createUpdateProjects(argThat(projects -> projects.contains(project)),
                        eq(user));
    }
}
