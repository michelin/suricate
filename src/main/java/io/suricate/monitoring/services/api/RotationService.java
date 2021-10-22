package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.dto.api.rotation.RotationRequestDto;
import io.suricate.monitoring.model.entities.Rotation;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.repositories.RotationRepository;
import io.suricate.monitoring.services.specifications.RotationSearchSpecification;
import io.suricate.monitoring.services.websocket.RotationWebSocketService;
import io.suricate.monitoring.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
     * String encryptor
     */
    private final StringEncryptor stringEncryptor;

    /**
     * Rotation web socket service
     */
    private final RotationWebSocketService rotationWebSocketService;

    /**
     * Constructor
     *
     * @param stringEncryptor           The string encryptor to inject
     * @param rotationRepository        The rotation repository
     * @param rotationWebSocketService  The rotation web socket service
     */
    public RotationService(@Qualifier("jasyptStringEncryptor") final StringEncryptor stringEncryptor,
                           RotationRepository rotationRepository,
                           RotationWebSocketService rotationWebSocketService) {
        this.stringEncryptor = stringEncryptor;
        this.rotationRepository = rotationRepository;
        this.rotationWebSocketService = rotationWebSocketService;
    }

    /**
     * Get the list of rotations
     *
     * @param search   The search string
     * @param pageable The page configurations
     * @return The list paginated
     */
    @Transactional(readOnly = true)
    public Page<Rotation> getAll(String search, Pageable pageable) {
        return this.rotationRepository.findAll(new RotationSearchSpecification(search), pageable);
    }

    /**
     * Get a rotation by id
     *
     * @param rotationToken The token of the rotation
     * @return The rotation
     */
    @Transactional(readOnly = true)
    public Optional<Rotation> getOneByToken(String rotationToken) {
        return this.rotationRepository.findByToken(rotationToken);
    }

    /**
     * Create a new rotation
     *
     * @param user     The user how create the project
     * @param rotation The rotation to create
     * @return The created rotation
     */
    @Transactional
    public Rotation create(User user, Rotation rotation) {
        rotation.getUsers().add(user);

        if (StringUtils.isBlank(rotation.getToken())) {
            rotation.setToken(stringEncryptor.encrypt(UUID.randomUUID().toString()));
        }

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
        return this.rotationRepository.findByUsersIdOrderByName(user.getId());
    }

    /**
     * Method used to delete a rotation with its ID
     *
     * @param rotation The rotation to delete
     */
    @Transactional
    public void deleteRotation(Rotation rotation) {
        this.rotationWebSocketService.disconnectAllClientsFromRotation(rotation.getToken());

        this.rotationRepository.delete(rotation);
    }

    /**
     * Update a rotation
     *
     * @param rotation The rotation to update
     * @param rotationRequestDto The new information
     */
    @Transactional
    public void updateRotation(Rotation rotation, RotationRequestDto rotationRequestDto) {
        if (StringUtils.isNotBlank(rotationRequestDto.getName())) {
            rotation.setName(rotationRequestDto.getName());
        }

        this.rotationRepository.save(rotation);
    }

    /**
     * Delete a user from a rotation
     *
     * @param user     The user to delete
     * @param rotation The rotation related
     */
    @Transactional
    public void deleteUserFromRotation(User user, Rotation rotation) {
        rotation.getUsers().remove(user);
        this.rotationRepository.save(rotation);
    }

    /**
     * Add a user to a rotation
     *
     * @param user     The user to add
     * @param rotation The rotation to edit
     */
    @Transactional
    public void addUserToRotation(User user, Rotation rotation) {
        rotation.getUsers().add(user);
        this.rotationRepository.save(rotation);
    }

    /**
     * Check if the connected user can access to this rotation
     *
     * @param rotation       The rotation
     * @param authentication The connected user
     * @return True if he can, false otherwise
     */
    public boolean isConnectedUserCanAccessToRotation(final Rotation rotation, final Authentication authentication) {
        return SecurityUtils.isAdmin(authentication)
                || rotation.getUsers().stream().anyMatch(currentUser -> currentUser.getUsername().equalsIgnoreCase(authentication.getName()));
    }
}
