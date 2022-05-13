package io.suricate.monitoring.repositories;

import io.suricate.monitoring.model.entities.ProjectGrid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectGridRepository extends JpaRepository<ProjectGrid, Long>, JpaSpecificationExecutor<ProjectGrid> {
    /**
     * Delete a grid by its id and the project id
     * @param projectId the project id
     * @param id        the widget instance id
     */
    void deleteByProjectIdAndId(Long projectId, Long id);

    /**
     * Delete grid by the project id
     * @param projectId the project id
     */
    void deleteByProjectId(Long projectId);
}
