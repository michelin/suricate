package com.michelin.suricate.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
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

        when(repositoryService.getAll(any(), any()))
            .thenReturn(new PageImpl<>(Collections.singletonList(repository)));
        when(repositoryMapper.toRepositoryDtoNoWidgets(any()))
            .thenReturn(repositoryResponseDto);

        Page<RepositoryResponseDto> actual = repositoryController.getAll("search", Pageable.unpaged());

        assertThat(actual).isNotEmpty();
        assertThat(actual.get()).hasSize(1);
        assertThat(actual.get().toList().get(0)).isEqualTo(repositoryResponseDto);
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
        when(repositoryMapper.toRepositoryEntity(any(), any()))
            .thenReturn(repository);
        when(repositoryMapper.toRepositoryDtoNoWidgets(any()))
            .thenReturn(repositoryResponseDto);

        ResponseEntity<RepositoryResponseDto> actual = repositoryController.createOne(repositoryRequestDto);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(actual.getBody()).isEqualTo(repositoryResponseDto);

        verify(gitService)
            .updateWidgetFromEnabledGitRepositories();
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
        when(repositoryMapper.toRepositoryEntity(any(), any()))
            .thenReturn(repository);
        when(repositoryMapper.toRepositoryDtoNoWidgets(any()))
            .thenReturn(repositoryResponseDto);

        ResponseEntity<RepositoryResponseDto> actual = repositoryController.createOne(repositoryRequestDto);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(actual.getBody()).isEqualTo(repositoryResponseDto);

        verify(gitService, times(0))
            .updateWidgetFromEnabledGitRepositories();
    }

    @Test
    void shouldGetOneByIdNotFound() {
        RepositoryResponseDto repositoryResponseDto = new RepositoryResponseDto();
        repositoryResponseDto.setId(1L);

        Repository repository = new Repository();
        repository.setId(1L);

        when(repositoryService.getOneById(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> repositoryController.getOneById(1L))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Repository '1' not found");
    }

    @Test
    void shouldGetOneById() {
        RepositoryResponseDto repositoryResponseDto = new RepositoryResponseDto();
        repositoryResponseDto.setId(1L);

        Repository repository = new Repository();
        repository.setId(1L);

        when(repositoryService.getOneById(any()))
            .thenReturn(Optional.of(repository));
        when(repositoryMapper.toRepositoryDtoNoWidgets(any()))
            .thenReturn(repositoryResponseDto);

        ResponseEntity<RepositoryResponseDto> actual = repositoryController.getOneById(1L);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isEqualTo(repositoryResponseDto);
    }

    @Test
    void shouldUpdateOneByIdNotFound() {
        RepositoryRequestDto repositoryRequestDto = new RepositoryRequestDto();
        repositoryRequestDto.setName("name");

        when(repositoryService.existsById(any()))
            .thenReturn(false);

        assertThatThrownBy(() -> repositoryController.updateOneById(1L, repositoryRequestDto, true))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Repository '1' not found");
    }

    @Test
    void shouldUpdateOneByIdSyncDisabled() throws GitAPIException, IOException {
        RepositoryRequestDto repositoryRequestDto = new RepositoryRequestDto();
        repositoryRequestDto.setName("name");

        Repository repository = new Repository();
        repository.setId(1L);

        when(repositoryService.existsById(any()))
            .thenReturn(true);
        when(repositoryMapper.toRepositoryEntity(any(), any()))
            .thenReturn(repository);

        ResponseEntity<Void> actual = repositoryController.updateOneById(1L, repositoryRequestDto, true);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void shouldUpdateOneByIdSyncDisabledOnRepo() throws GitAPIException, IOException {
        RepositoryRequestDto repositoryRequestDto = new RepositoryRequestDto();
        repositoryRequestDto.setName("name");

        Repository repository = new Repository();
        repository.setId(1L);
        repository.setEnabled(false);

        when(repositoryService.existsById(any()))
            .thenReturn(true);
        when(repositoryMapper.toRepositoryEntity(any(), any()))
            .thenReturn(repository);

        ResponseEntity<Void> actual = repositoryController.updateOneById(1L, repositoryRequestDto, false);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void shouldUpdateOneByIdSyncEnabled() throws GitAPIException, IOException {
        RepositoryRequestDto repositoryRequestDto = new RepositoryRequestDto();
        repositoryRequestDto.setName("name");

        Repository repository = new Repository();
        repository.setId(1L);
        repository.setEnabled(true);

        when(repositoryService.existsById(any()))
            .thenReturn(true);
        when(repositoryMapper.toRepositoryEntity(any(), any()))
            .thenReturn(repository);

        ResponseEntity<Void> actual = repositoryController.updateOneById(1L, repositoryRequestDto, false);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void shouldSynchronize() throws GitAPIException, IOException {
        ResponseEntity<Void> actual = repositoryController.synchronize();

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void shouldGetRepositoryWidgetNotFound() {
        when(repositoryService.getOneById(any()))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> repositoryController.getRepositoryWidget(1L))
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Repository '1' not found");
    }

    @Test
    void shouldGetRepositoryWidget() {
        Repository repository = new Repository();
        repository.setId(1L);

        WidgetResponseDto widgetResponseDto = new WidgetResponseDto();
        widgetResponseDto.setId(1L);

        when(repositoryService.getOneById(any()))
            .thenReturn(Optional.of(repository));
        when(widgetMapper.toWidgetsDtos(any()))
            .thenReturn(Collections.singletonList(widgetResponseDto));

        ResponseEntity<List<WidgetResponseDto>> actual = repositoryController.getRepositoryWidget(1L);

        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).contains(widgetResponseDto);
    }
}
