package io.suricate.monitoring.services.api;

import com.google.common.collect.Sets;
import io.suricate.monitoring.model.dto.api.rotation.RotationRequestDto;
import io.suricate.monitoring.model.dto.api.rotationproject.RotationProjectRequestDto;
import io.suricate.monitoring.model.dto.nashorn.NashornResponse;
import io.suricate.monitoring.model.dto.websocket.UpdateEvent;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.Rotation;
import io.suricate.monitoring.model.entities.RotationProject;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.model.enums.UpdateType;
import io.suricate.monitoring.repositories.RotationRepository;
import io.suricate.monitoring.services.mapper.RotationMapper;
import io.suricate.monitoring.services.mapper.RotationProjectMapper;
import io.suricate.monitoring.services.nashorn.tasks.NashornRequestWidgetExecutionAsyncTask;
import io.suricate.monitoring.services.rotation.RotationExecutionScheduler;
import io.suricate.monitoring.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Rotation service
 */
@Service
public class RotationService {
    /**
     * Project service
     */
    private final ProjectService projectService;

    /**
     * Rotation repository
     */
    private final RotationRepository rotationRepository;

    /**
     * The rotation project mapper
     */
    private final RotationProjectMapper rotationProjectMapper;

    /**
     * String encryptor
     */
    private final StringEncryptor stringEncryptor;

    private final RotationExecutionScheduler rotationExecutionScheduler;

    /**
     * Constructor
     *
     * @param stringEncryptor The string encryptor to inject
     * @param rotationRepository The rotation repository
     * @param projectService The project service
     * @param rotationProjectMapper The rotation project mapper
     */
    public RotationService(@Qualifier("jasyptStringEncryptor") final StringEncryptor stringEncryptor,
                           RotationRepository rotationRepository,
                           ProjectService projectService,
                           RotationProjectMapper rotationProjectMapper,
                           RotationExecutionScheduler rotationExecutionScheduler) {
        this.stringEncryptor = stringEncryptor;
        this.rotationRepository = rotationRepository;
        this.projectService = projectService;
        this.rotationProjectMapper = rotationProjectMapper;
        this.rotationExecutionScheduler = rotationExecutionScheduler;
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
     * @param rotation The rotation to create
     * @return The created rotation
     */
    @Transactional
    public Rotation create(Rotation rotation) {
        if (StringUtils.isBlank(rotation.getToken())) {
            rotation.setToken(stringEncryptor.encrypt(UUID.randomUUID().toString()));
        }

        Rotation createdRotation = this.rotationRepository.save(rotation);

        createdRotation.getRotationProjects().forEach(rotationProject ->
                rotationProject.setRotation(createdRotation));

        return createdRotation;
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
     * Method used to delete a rotation with its ID
     *
     * @param rotation The rotation to delete
     */
    @Transactional
    public void deleteRotation(Rotation rotation) {
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
        
        // Remove rotation projects
        List<String> projectTokens = rotationRequestDto.getRotationProjectRequests()
                .stream()
                .map(RotationProjectRequestDto::getProjectToken)
                .collect(Collectors.toList());

        rotation.getRotationProjects()
                .removeIf(rotationProject -> !projectTokens
                    .contains(rotationProject.getProject().getToken()));

        // Update rotation projects
        rotation.getRotationProjects()
                .forEach(rotationProject -> rotationRequestDto.getRotationProjectRequests()
                        .stream()
                        .filter(rotationProjectRequestDto -> rotationProject.getProject().getToken().equals(rotationProjectRequestDto.getProjectToken()))
                        .findFirst()
                        .ifPresent(newRotationProjectDto -> rotationProject.setRotationSpeed(newRotationProjectDto.getRotationSpeed())));

        // Create new rotation projects
        rotationRequestDto.getRotationProjectRequests()
                .removeIf(rotationProjectRequestDto -> rotation.getRotationProjects()
                        .stream()
                        .anyMatch(rotationProject -> rotationProject.getProject().getToken().equals(rotationProjectRequestDto.getProjectToken())));

        rotation.getRotationProjects().addAll(rotationRequestDto.getRotationProjectRequests()
                .stream()
                .map(rotationProjectRequestDto -> {
                    RotationProject rotationProject = this.rotationProjectMapper.toRotationProjectEntity(rotationProjectRequestDto);
                    rotationProject.setRotation(rotation);
                    return rotationProject;
                })
                .collect(Collectors.toList()));

        this.rotationRepository.save(rotation);
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

    /**
     * Schedule a rotation of projects
     *
     * @param rotation The rotation to schedule
     * @param current The current project of the rotation
     * @param iterator An iterator pointing on the next projects to rotate
     * @param screenCode The screen code where to push the next project
     */
    public void scheduleRotation(Rotation rotation, RotationProject current, Iterator<RotationProject> iterator,
                                 String screenCode) {
        this.rotationExecutionScheduler.scheduleRotation(rotation, current, iterator, screenCode);
    }
}
