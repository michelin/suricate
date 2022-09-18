/*
 * Copyright 2012-2021 the original author or authors.
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

import io.suricate.monitoring.model.entities.Repository;
import io.suricate.monitoring.repositories.RepositoryRepository;
import io.suricate.monitoring.services.specifications.RepositorySearchSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private RepositoryRepository repositoryRepository;

    /**
     * Get all repositories
     *
     * @param search The search string
     * @param pageable The pageable object
     * @return The paginated list of repositories
     */
    @Transactional(readOnly = true)
    public Page<Repository> getAll(String search, Pageable pageable) {
        return repositoryRepository.findAll(new RepositorySearchSpecification(search), pageable);
    }

    /**
     * Get the full list of repository by enabled
     *
     * @param enabled Tru if we want every enabled repositories
     * @return The related list
     */
    @Transactional(readOnly = true)
    public Optional<List<Repository>> findAllByEnabledOrderByPriorityDescCreatedDateAsc(final boolean enabled) {
        return repositoryRepository.findAllByEnabledOrderByPriorityDescCreatedDateAsc(enabled);
    }

    /**
     * Get the repository by id
     *
     * @param repositoryId The repository id to find
     * @return The repository as optional
     */
    @Transactional(readOnly = true)
    public Optional<Repository> getOneById(final Long repositoryId) {
        return repositoryRepository.findById(repositoryId);
    }

    /**
     * Get the repository by name
     * @param name The repository name
     * @return The repository as optional
     */
    @Transactional(readOnly = true)
    public Optional<Repository> findByName(final String name) {
        return repositoryRepository.findByName(name);
    }

    /**
     * Check if the repository exists
     *
     * @param repositoryId The repository id to check
     * @return True if exist false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsById(final Long repositoryId) {
        return this.repositoryRepository.existsById(repositoryId);
    }

    /**
     * Check if a repository exists by given name
     * @param name The repository name
     * @return true if it is, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean existsByName(final String name) {
        return this.repositoryRepository.existsByName(name);
    }

    /**
     * Add or update a repository
     * @param repository The repository to process
     */
    @Transactional
    public void addOrUpdateRepository(Repository repository) {
        repositoryRepository.save(repository);
    }

    /**
     * Add or update a list of repositories
     * @param repositories All the repositories to add/update
     */
    @Transactional
    public void addOrUpdateRepositories(List<Repository> repositories) {
        repositoryRepository.saveAll(repositories);
    }
}
