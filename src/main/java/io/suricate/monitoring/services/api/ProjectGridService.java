package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.ProjectGrid;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.repositories.ProjectGridRepository;
import io.suricate.monitoring.repositories.ProjectRepository;
import io.suricate.monitoring.services.websocket.DashboardWebSocketService;
import io.suricate.monitoring.utils.logging.LogExecutionTime;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ProjectGridService {

    /**
     * Project repository
     */
    private final ProjectGridRepository projectGridRepository;

    /**
     * Constructor
     * @param projectGridRepository The project grid repository to inject
     */
    @Autowired
    public ProjectGridService(final ProjectGridRepository projectGridRepository) {
        this.projectGridRepository = projectGridRepository;
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
     * Persist a given list of project grids
     *
     * @param projectGrids The list of project grids
     */
    @Transactional
    public Collection<ProjectGrid> createProjectGrid(List<ProjectGrid> projectGrids) {
        List<ProjectGrid> createdProjectGrids = new ArrayList<>();
        projectGridRepository.saveAll(projectGrids).forEach(createdProjectGrids::add);
        return createdProjectGrids;
    }

    /**
     * Delete a grid by project id and id
     *
     * @param projectId The project id
     * @param id The grid id
     */
    @Transactional
    public void deleteByProjectIdAndId(Long projectId, Long id) { projectGridRepository.deleteByProjectIdAndId(projectId, id); }
}
