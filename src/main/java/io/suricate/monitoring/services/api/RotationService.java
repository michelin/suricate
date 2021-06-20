package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.entities.Rotation;
import io.suricate.monitoring.model.entities.RotationProject;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.repositories.RotationRepository;
import io.suricate.monitoring.utils.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Rotation service
 */
@Service
public class RotationService {
    /**
     * Rotation repository
     */
    private final RotationRepository rotationRepository;

    /**
     * Rotation repository
     */
    private final ProjectService projectService;

    /**
     * Constructor
     *
     * @param rotationRepository The rotation repository
     * @parama projectService The project service
     */
    public RotationService(RotationRepository rotationRepository,
                           ProjectService projectService) {
        this.rotationRepository = rotationRepository;
        this.projectService = projectService;
    }

    /**
     * Get a rotation by id
     *
     * @param id The id of the rotation
     * @return The rotation
     */
    @Transactional(readOnly = true)
    public Optional<Rotation> getOneById(Long id) {
        return this.rotationRepository.findById(id);
    }

    /**
     * Create a new rotation
     *
     * @param rotation The rotation to create
     * @return The created rotation
     */
    @Transactional
    public Rotation create(Rotation rotation) {
        return this.rotationRepository.save(rotation);
    }

    /**
     * Get all projects by user
     *
     * @param user The user
     * @return A list of projects
     */
    @Transactional(readOnly = true)
    public List<Rotation> getAllByUser(User user) {
        return this.rotationRepository.findDistinctByRotationProjectsProjectUsersId(user.getId());
    }

    /**
     * Check if the connected user can access to this rotation
     *
     * If the user can access to one dashboard of the rotation, then he can
     * access to the rotation
     *
     * @param rotation       The rotation
     * @param authentication The connected user
     * @return True if he can, false otherwise
     */
    public boolean isConnectedUserCanAccessToRotation(final Rotation rotation, final Authentication authentication) {
        if (SecurityUtils.isAdmin(authentication)) {
            return true;
        }

        for (RotationProject rotationProject : rotation.getRotationProjects()) {
            if (this.projectService.isConnectedUserCanAccessToProject(rotationProject.getProject(), authentication)) {
                return true;
            }
        }

        return false;
    }
}
