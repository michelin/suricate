package io.suricate.monitoring.repositories;

import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.Rotation;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RotationRepository extends CrudRepository<Rotation, Long>, JpaSpecificationExecutor<Rotation> {
    /**
     * Find rotation by user id
     *
     * @param id The user id
     * @return List of related rotation ordered by name
     */
    List<Rotation> findDistinctByRotationProjectsProjectUsersId(Long id);

    /**
     * Find a rotation by token
     *
     * @param token The token
     * @return The rotation
     */
    @EntityGraph(attributePaths = {"rotationProjects.project.screenshot", "rotationProjects.project.widgets", "rotationProjects.project.users"})
    Optional<Rotation> findByToken(final String token);
}
