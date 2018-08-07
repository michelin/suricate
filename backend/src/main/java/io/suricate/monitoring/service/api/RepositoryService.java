/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.suricate.monitoring.service.api;

import io.suricate.monitoring.model.entity.widget.Repository;
import io.suricate.monitoring.repository.RepositoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Manage the repository
 */
@Service
public class RepositoryService {

    /**
     * Class logger
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(RepositoryService.class);

    /**
     * The repository for repository
     */
    private final RepositoryRepository repositoryRepository;

    /**
     * Constructor
     *
     * @param repositoryRepository The repository used for manage repository to inject
     */
    @Autowired
    public RepositoryService(final RepositoryRepository repositoryRepository) {
        this.repositoryRepository = repositoryRepository;
    }

    /**
     * Find the list of repositories order by name
     *
     * @return The list of repositories ordered by name
     */
    public Optional<List<Repository>> getAllOrderByName() {
        return repositoryRepository.findAllByOrderByName();
    }

    /**
     * Get the full list of repository by enabled
     *
     * @param enabled Tru if we want every enabled repositories
     * @return The related list
     */
    public Optional<List<Repository>> getAllByEnabledOrderByName(final boolean enabled) {
        return repositoryRepository.findAllByEnabledOrderByName(enabled);
    }

    /**
     * Get the repository by name
     *
     * @param repositoryId The repository id to find
     * @return The repository as optional
     */
    public Optional<Repository> getOneById(final Long repositoryId) {
        return repositoryRepository.findById(repositoryId);
    }

    /**
     * Check if the repository exists
     *
     * @param repositoryId The repository id to check
     * @return True if exist false otherwise
     */
    public boolean existsById(final Long repositoryId) {
        return this.repositoryRepository.existsById(repositoryId);
    }

    /**
     * Add or update a repository
     *
     * @param repository The repository to process
     */
    public void addOrUpdateRepository(Repository repository) {
        repositoryRepository.save(repository);
    }
}
