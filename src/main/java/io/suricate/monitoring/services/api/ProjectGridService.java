package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.dto.api.projectgrid.ProjectGridRequestDto;
import io.suricate.monitoring.model.dto.websocket.UpdateEvent;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.ProjectGrid;
import io.suricate.monitoring.model.enums.UpdateType;
import io.suricate.monitoring.repositories.ProjectGridRepository;
import io.suricate.monitoring.repositories.ProjectRepository;
import io.suricate.monitoring.services.nashorn.scheduler.NashornRequestWidgetExecutionScheduler;
import io.suricate.monitoring.services.websocket.DashboardWebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectGridService {
    /**
     * The application context
     */
    private final ApplicationContext ctx;

    /**
     * The dashboard websocket service
     */
    private final DashboardWebSocketService dashboardWebsocketService;

    /**
     * Project repository
     */
    private final ProjectRepository projectRepository;

    /**
     * Project repository
     */
    private final ProjectGridRepository projectGridRepository;

    /**
     * Constructor
     *
     * @param projectGridRepository The project grid repository to inject
     * @param projectRepository The project repository to inject
     * @param dashboardWebSocketService The dashboard websocket service
     * @param ctx The application context
     */
    @Autowired
    public ProjectGridService(final ProjectGridRepository projectGridRepository,
                              final ProjectRepository projectRepository,
                              final DashboardWebSocketService dashboardWebSocketService,
                              final ApplicationContext ctx) {
        this.projectRepository = projectRepository;
        this.projectGridRepository = projectGridRepository;
        this.dashboardWebsocketService = dashboardWebSocketService;
        this.ctx = ctx;
    }

    /**
     * Get a project grid by the id
     *
     * @param id The id of the project grid
     * @return The project grid associated
     */
    @Transactional(readOnly = true)
    public Optional<ProjectGrid> getOneById(Long id) {
        return projectGridRepository.findById(id);
    }

    /**
     * Persist a given project grid
     *
     * @param projectGrid The grid
     */
    @Transactional
    public ProjectGrid create(ProjectGrid projectGrid) {
        return projectGridRepository.save(projectGrid);
    }

    /**
     * Persist a given list of project grids
     *
     * @param projectGrids The grids
     */
    @Transactional
    public List<ProjectGrid> createAll(List<ProjectGrid> projectGrids) {
        List<ProjectGrid> results = new ArrayList<>();
        projectGridRepository.saveAll(projectGrids).forEach(results::add);
        return results;
    }

    /**
     * Persist a given list of project grids
     *
     * @param project The project to update
     * @param projectGridRequestDto The new data as DTO
     */
    @Transactional
    public void updateAll(Project project, ProjectGridRequestDto projectGridRequestDto) {
        project.setDisplayProgressBar(projectGridRequestDto.isDisplayProgressBar());

        projectRepository.save(project);

        project.getGrids().forEach(projectGrid -> {
            Optional<ProjectGridRequestDto.GridRequestDto> gridRequestDtoOptional = projectGridRequestDto.getGrids()
                    .stream()
                    .filter(dto -> dto.getId().equals(projectGrid.getId()))
                    .findFirst();

            gridRequestDtoOptional.ifPresent(gridRequestDto -> projectGrid.setTime(gridRequestDto.getTime()));
        });

        projectGridRepository.saveAll(project.getGrids());
    }

    /**
     * Delete a grid by project id and id
     *
     * @param project The project
     * @param id The grid id
     */
    @Transactional
    public void deleteByProjectIdAndId(Project project, Long id) {
        Optional<ProjectGrid> projectGridOptional = getOneById(id);

        if (projectGridOptional.isPresent()) {
            ProjectGrid projectGrid = projectGridOptional.get();

            if (!projectGrid.getWidgets().isEmpty()) {
                ctx.getBean(NashornRequestWidgetExecutionScheduler.class).cancelWidgetsExecutionByGrid(projectGrid);
            }

            projectGridRepository.deleteByProjectIdAndId(project.getId(), id);

            UpdateEvent updateEvent = UpdateEvent.builder()
                    .type(UpdateType.REFRESH_DASHBOARD)
                    .build();

            dashboardWebsocketService.sendEventToProjectSubscribers(project.getToken(), updateEvent);
        }
    }
}
