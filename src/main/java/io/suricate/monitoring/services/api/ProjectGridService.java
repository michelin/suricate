package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.dto.api.projectgrid.ProjectGridRequestDto;
import io.suricate.monitoring.model.entities.ProjectGrid;
import io.suricate.monitoring.repositories.ProjectGridRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
    public Collection<ProjectGrid> createAll(List<ProjectGrid> projectGrids) {
        List<ProjectGrid> createdProjectGrids = new ArrayList<>();
        projectGridRepository.saveAll(projectGrids).forEach(createdProjectGrids::add);
        return createdProjectGrids;
    }

    /**
     * Persist a given list of project grids
     *
     * @param projectGrids The list of project grids
     * @param projectRequestDtos The list of project grids as DTO
     */
    @Transactional
    public void updateAll(Collection<ProjectGrid> projectGrids, List<ProjectGridRequestDto> projectRequestDtos) {
        projectGrids.forEach(projectGrid -> {
            Optional<ProjectGridRequestDto> projectGridDto = projectRequestDtos
                    .stream()
                    .filter(dto -> dto.getId().equals(projectGrid.getId()))
                    .findFirst();

            projectGridDto.ifPresent(projectGridRequestDto -> projectGrid.setTime(projectGridRequestDto.getTime()));
        });

        projectGridRepository.saveAll(projectGrids);
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
