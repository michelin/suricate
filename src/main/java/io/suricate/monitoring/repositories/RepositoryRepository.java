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

package io.suricate.monitoring.repositories;

import io.suricate.monitoring.model.entities.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * Repositories repository
 */
@org.springframework.stereotype.Repository
public interface RepositoryRepository extends JpaRepository<Repository, Long>, JpaSpecificationExecutor<Repository> {
    /**
     * Find all by enabled order by name
     * @param enabled True if we want every enabled repository, false otherwise
     * @return The list of repositories
     */
    Optional<List<Repository>> findAllByEnabledOrderByPriorityDesc(final boolean enabled);

    /**
     * Find repository by name
     * @param name The repository name
     * @return An optional repository
     */
    Optional<Repository> findByName(final String name);

    /**
     * Check if a repository exists by given name
     * @param name The repository name
     * @return true if it is, false otherwise
     */
    boolean existsByName(final String name);
}
