package io.suricate.monitoring.repositories;

import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.Rotation;
import io.suricate.monitoring.model.entities.Widget;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RotationRepository extends CrudRepository<Rotation, Long>, JpaSpecificationExecutor<Rotation> {
    /**
     * Find all paginated rotations
     *
     * @param specification The specification to apply
     * @param pageable The pageable to apply
     * @return The paginated rotations
     */
    @NotNull
    @EntityGraph(attributePaths = {"rotationProjects.project.widgets", "rotationProjects.project.screenshot"})
    Page<Rotation> findAll(Specification<Rotation> specification, @NotNull Pageable pageable);

    /**
     * Find rotation by user id
     *
     * @param id The user id
     * @return List of related rotation ordered by name
     */
    @EntityGraph(attributePaths = {"rotationProjects.project.widgets", "rotationProjects.project.screenshot"})
    List<Rotation> findByUsersIdOrderByName(Long id);

    /**
     * Find a rotation by token
     *
     * @param token The token
     * @return The rotation
     */
    @EntityGraph(attributePaths = {"rotationProjects.project.screenshot",
                                   "rotationProjects.project.widgets",
                                   "rotationProjects.project.users",
                                   "users.roles"})
    Optional<Rotation> findByToken(final String token);
}
