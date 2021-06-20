package io.suricate.monitoring.repositories;

import io.suricate.monitoring.model.entities.Rotation;
import io.suricate.monitoring.model.entities.RotationProject;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface RotationProjectRepository extends CrudRepository<RotationProject, Long>, JpaSpecificationExecutor<RotationProject> {
}
