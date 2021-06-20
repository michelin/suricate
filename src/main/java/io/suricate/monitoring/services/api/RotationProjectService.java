package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.entities.Rotation;
import io.suricate.monitoring.model.entities.RotationProject;
import io.suricate.monitoring.repositories.RotationProjectRepository;
import io.suricate.monitoring.repositories.RotationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Rotation project service
 */
@Service
public class RotationProjectService {
    /**
     * Rotation repository
     */
    private final RotationProjectRepository rotationProjectRepository;

    /**
     * Constructor
     *
     * @param rotationProjectRepository The rotation project repository
     */
    public RotationProjectService(RotationProjectRepository rotationProjectRepository) {
        this.rotationProjectRepository = rotationProjectRepository;
    }

    /**
     * Create all the given rotation projects
     *
     * @param rotationProjects The rotation projects to create
     * @return The list of created rotation projects
     */
    @Transactional
    public List<RotationProject> createAll(Set<RotationProject> rotationProjects) {
        return (List<RotationProject>) this.rotationProjectRepository.saveAll(rotationProjects);
    }
}
