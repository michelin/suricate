package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.dto.api.projectgrid.ProjectGridRequestDto;
import io.suricate.monitoring.model.dto.websocket.UpdateEvent;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.ProjectGrid;
import io.suricate.monitoring.model.entities.ProjectWidget;
import io.suricate.monitoring.model.enums.UpdateType;
import io.suricate.monitoring.repositories.ProjectGridRepository;
import io.suricate.monitoring.repositories.ProjectRepository;
import io.suricate.monitoring.services.nashorn.scheduler.NashornRequestWidgetExecutionScheduler;
import io.suricate.monitoring.services.websocket.DashboardWebSocketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    private NashornRequestWidgetExecutionScheduler nashornRequestWidgetExecutionScheduler;

    @InjectMocks
    private ProjectGridService projectGridService;

    @Test
    void shouldGetOneById() {
        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);

        when(projectGridRepository.findById(any()))
                .thenReturn(Optional.of(projectGrid));

        Optional<ProjectGrid> actual = projectGridService.getOneById(1L);

        assertThat(actual)
                .isPresent()
                .contains(projectGrid);

        verify(projectGridRepository, times(1))
                .findById(1L);
    }

    @Test
    void shouldFindByIdAndProjectToken() {
        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);

        when(projectGridRepository.findByIdAndProjectToken(any(), any()))
                .thenReturn(Optional.of(projectGrid));

        Optional<ProjectGrid> actual = projectGridService.findByIdAndProjectToken(1L, "token");

        assertThat(actual)
                .isPresent()
                .contains(projectGrid);

        verify(projectGridRepository, times(1))
                .findByIdAndProjectToken(1L, "token");
    }

    @Test
    void shouldCreate() {
        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);

        when(projectGridRepository.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));

        ProjectGrid actual = projectGridService.create(projectGrid);

        assertThat(actual)
                .isEqualTo(projectGrid);

        verify(projectGridRepository, times(1))
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

       assertThat(new ArrayList<>(project.getGrids()).get(0).getTime())
               .isEqualTo(10);

        verify(projectRepository, times(1))
                .save(project);
        verify(projectGridRepository, times(1))
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
        when(ctx.getBean(NashornRequestWidgetExecutionScheduler.class))
                .thenReturn(nashornRequestWidgetExecutionScheduler);
        doNothing().when(nashornRequestWidgetExecutionScheduler)
                .cancelWidgetsExecutionByGrid(any());

        projectGridService.deleteByProjectIdAndId(project, 1L);

        verify(projectGridRepository, times(1))
                .deleteByProjectIdAndId(1L, 1L);
        verify(dashboardWebsocketService, times(1))
                .sendEventToProjectSubscribers("token", UpdateEvent.builder()
                        .type(UpdateType.REFRESH_DASHBOARD)
                        .build());
        verify(nashornRequestWidgetExecutionScheduler, times(1))
                .cancelWidgetsExecutionByGrid(projectGrid);
    }
}
