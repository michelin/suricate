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

import io.suricate.monitoring.model.entities.RotationProject;
import io.suricate.monitoring.repositories.RotationProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
     * Constructor
     *
     * @param rotationProjectRepository The rotation project repository
     */
    public RotationProjectService(RotationProjectRepository rotationProjectRepository) {
        this.rotationProjectRepository = rotationProjectRepository;
    }

    /**
     * Add projects to the rotation
     *
     * @param rotationProjects The projects to add
     */
    @Transactional
    public void addProjectsToRotation(List<RotationProject> rotationProjects) {
        this.rotationProjectRepository.saveAll(rotationProjects);
    }
}
