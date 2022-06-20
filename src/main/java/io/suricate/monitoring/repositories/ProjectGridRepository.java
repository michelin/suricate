package io.suricate.monitoring.repositories;

import io.suricate.monitoring.model.entities.ProjectGrid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectGridRepository extends JpaRepository<ProjectGrid, Long>, JpaSpecificationExecutor<ProjectGrid> {
    /**
     * Delete a grid by its id and the project id
     * @param projectId the project id
     * @param id        the widget instance id
     */
    void deleteByProjectIdAndId(Long projectId, Long id);

    /**
     * Find a grid by id and project token
     * @param id The ID
     * @param token The project token
     * @return The grid
     */
    Optional<ProjectGrid> findByIdAndProjectToken(Long id, String token);
}
