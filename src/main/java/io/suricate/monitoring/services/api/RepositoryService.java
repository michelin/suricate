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

package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.entity.widget.Repository;
import io.suricate.monitoring.repositories.RepositoryRepository;
import io.suricate.monitoring.services.specifications.RepositorySearchSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Manage the repository
 */
@Service
public class RepositoryService {
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

    public Page<Repository> getAll(String search, Pageable pageable) {
        return repositoryRepository.findAll(new RepositorySearchSpecification(search), pageable);
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
