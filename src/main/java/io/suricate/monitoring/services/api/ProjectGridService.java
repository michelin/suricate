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

import java.util.Optional;

@Service
public class ProjectGridService {
    /**
     * The application context
     */
    @Autowired
    private ApplicationContext ctx;

    /**
     * The dashboard websocket service
     */
    @Autowired
    private DashboardWebSocketService dashboardWebsocketService;

    /**
     * Project repository
     */
    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Project repository
     */
    @Autowired
    private ProjectGridRepository projectGridRepository;

    /**
     * Get a project grid by the id
     * @param id The id of the project grid
     * @return The project grid associated
     */
    @Transactional(readOnly = true)
    public Optional<ProjectGrid> getOneById(Long id) {
        return projectGridRepository.findById(id);
    }

    /**
     * Find a grid by id and project token
     * @param id The ID
     * @param token The project token
     * @return The grid
     */
    @Transactional(readOnly = true)
    public Optional<ProjectGrid> findByIdAndProjectToken(Long id, String token) {
        return projectGridRepository.findByIdAndProjectToken(id, token);
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
