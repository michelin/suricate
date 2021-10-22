/*
 *
 *  * Copyright 2012-2018 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.entities.Rotation;
import io.suricate.monitoring.model.entities.RotationProject;
import io.suricate.monitoring.repositories.RotationProjectRepository;
import io.suricate.monitoring.services.websocket.RotationWebSocketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Rotation project service
 */
@Service
public class RotationProjectService {
    /**
     * Rotation project repository
     */
    private final RotationProjectRepository rotationProjectRepository;

    /**
     * Rotation web socket service
     */
    private final RotationWebSocketService rotationWebSocketService;

    /**
     * Constructor
     *
     * @param rotationProjectRepository The rotation project repository
     * @param rotationWebSocketService  The rotation web socket service
     */
    public RotationProjectService(RotationProjectRepository rotationProjectRepository,
                                  RotationWebSocketService rotationWebSocketService) {
        this.rotationProjectRepository = rotationProjectRepository;
        this.rotationWebSocketService = rotationWebSocketService;
    }

    /**
     * Add projects to the rotation
     *
     * @param rotation The rotation
     * @param rotationProjects The projects to add
     */
    @Transactional
    public void addProjectsToRotation(Rotation rotation, List<RotationProject> rotationProjects) {
        List<RotationProject> toDelete = rotation.getRotationProjects()
                .stream()
                .filter(rotationProject -> rotationProjects
                        .stream()
                        .noneMatch(newRotationProject -> newRotationProject.getProject().getId().equals(rotationProject.getProject().getId()) &&
                                newRotationProject.getRotation().getId().equals(rotationProject.getRotation().getId())))
                .collect(Collectors.toList());

        toDelete.forEach(rotation.getRotationProjects()::remove);

        this.rotationProjectRepository.deleteAll(toDelete);

        List<RotationProject> toCreate = rotationProjects
                .stream()
                .filter(newRotationProject -> rotation.getRotationProjects()
                    .stream()
                    .noneMatch(rotationProject -> newRotationProject.getProject().getId().equals(rotationProject.getProject().getId()) &&
                            newRotationProject.getRotation().getId().equals(rotationProject.getRotation().getId())))
                .collect(Collectors.toList());

        rotation.getRotationProjects()
            .forEach(rotationProject -> {
                Optional<RotationProject> rotationProjectOptional = rotationProjects
                    .stream()
                    .filter(newRotationProject -> newRotationProject.getProject().getId().equals(rotationProject.getProject().getId()) &&
                        newRotationProject.getRotation().getId().equals(rotationProject.getRotation().getId()))
                    .findFirst();

                rotationProjectOptional.ifPresent(project -> rotationProject.setRotationSpeed(project.getRotationSpeed()));
            });

        this.rotationProjectRepository.saveAll(Stream
                .concat(toCreate.stream(), rotation.getRotationProjects().stream())
                .collect(Collectors.toList()));

        this.rotationWebSocketService.reloadAllConnectedClientsToARotation(rotation.getToken());
    }
}
