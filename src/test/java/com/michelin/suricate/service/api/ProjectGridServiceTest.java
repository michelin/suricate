package com.michelin.suricate.service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.projectgrid.ProjectGridRequestDto;
import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.model.entity.ProjectGrid;
import com.michelin.suricate.model.entity.ProjectWidget;
import com.michelin.suricate.model.enumeration.UpdateType;
import com.michelin.suricate.repository.ProjectGridRepository;
import com.michelin.suricate.repository.ProjectRepository;
import com.michelin.suricate.service.js.scheduler.JsExecutionScheduler;
import com.michelin.suricate.service.websocket.DashboardWebSocketService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
class ProjectGridServiceTest {
    @Mock
    private ApplicationContext ctx;

    @Mock
    private DashboardWebSocketService dashboardWebsocketService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectGridRepository projectGridRepository;

    @Mock
    private JsExecutionScheduler jsExecutionScheduler;

    @InjectMocks
    private ProjectGridService projectGridService;

    @Test
    void shouldGetOneById() {
        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);

        when(projectGridRepository.findById(any()))
            .thenReturn(Optional.of(projectGrid));

        Optional<ProjectGrid> actual = projectGridService.getOneById(1L);

        assertTrue(actual.isPresent());
        assertEquals(projectGrid, actual.get());

        verify(projectGridRepository)
            .findById(1L);
    }

    @Test
    void shouldFindByIdAndProjectToken() {
        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);

        when(projectGridRepository.findByIdAndProjectToken(any(), any()))
            .thenReturn(Optional.of(projectGrid));

        Optional<ProjectGrid> actual = projectGridService.findByIdAndProjectToken(1L, "token");

        assertTrue(actual.isPresent());
        assertEquals(projectGrid, actual.get());

        verify(projectGridRepository)
            .findByIdAndProjectToken(1L, "token");
    }

    @Test
    void shouldCreate() {
        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);

        when(projectGridRepository.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));

        ProjectGrid actual = projectGridService.create(projectGrid);

        assertEquals(projectGrid, actual);

        verify(projectGridRepository)
            .save(projectGrid);
    }

    @Test
    void shouldUpdateAll() {
        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);

        Project project = new Project();
        project.setId(1L);
        project.setGrids(Collections.singleton(projectGrid));

        ProjectGridRequestDto projectGridRequestDto = new ProjectGridRequestDto();
        ProjectGridRequestDto.GridRequestDto gridRequestDto = new ProjectGridRequestDto.GridRequestDto();
        gridRequestDto.setId(1L);
        gridRequestDto.setTime(10);
        projectGridRequestDto.setGrids(Collections.singletonList(gridRequestDto));

        when(projectRepository.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));
        when(projectGridRepository.saveAll(any()))
            .thenReturn(new ArrayList<>());

        projectGridService.updateAll(project, projectGridRequestDto);

        assertEquals(10, new ArrayList<>(project.getGrids()).getFirst().getTime());

        verify(projectRepository)
            .save(project);
        verify(projectGridRepository)
            .saveAll(Collections.singleton(projectGrid));
    }

    @Test
    void shouldDeleteByProjectIdAndIdWhenNotPresent() {
        when(projectGridRepository.findById(any()))
            .thenReturn(Optional.empty());

        projectGridService.deleteByProjectIdAndId(new Project(), 1L);

        verify(projectGridRepository, times(0))
            .deleteByProjectIdAndId(any(), any());
        verify(dashboardWebsocketService, times(0))
            .sendEventToProjectSubscribers(any(), any());
    }

    @Test
    void shouldDeleteByProjectIdAndId() {
        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setWidgets(Collections.singleton(projectWidget));

        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        when(projectGridRepository.findById(any()))
            .thenReturn(Optional.of(projectGrid));
        when(ctx.getBean(JsExecutionScheduler.class))
            .thenReturn(jsExecutionScheduler);

        projectGridService.deleteByProjectIdAndId(project, 1L);

        verify(projectGridRepository)
            .deleteByProjectIdAndId(1L, 1L);
        verify(dashboardWebsocketService)
            .sendEventToProjectSubscribers(eq("token"),
                argThat(event -> event.getType().equals(UpdateType.REFRESH_DASHBOARD)
                    && event.getDate() != null));
        verify(jsExecutionScheduler)
            .cancelWidgetsExecutionByGrid(projectGrid);
    }

    @Test
    void shouldDeleteByProjectIdAndIdNoProjectWidget() {
        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);

        Project project = new Project();
        project.setId(1L);
        project.setToken("token");

        when(projectGridRepository.findById(any()))
            .thenReturn(Optional.of(projectGrid));

        projectGridService.deleteByProjectIdAndId(project, 1L);

        verify(projectGridRepository)
            .deleteByProjectIdAndId(1L, 1L);
        verify(dashboardWebsocketService)
            .sendEventToProjectSubscribers(eq("token"),
                argThat(event -> event.getType().equals(UpdateType.REFRESH_DASHBOARD)
                    && event.getDate() != null));
        verify(jsExecutionScheduler, times(0))
            .cancelWidgetsExecutionByGrid(any());
    }
}
