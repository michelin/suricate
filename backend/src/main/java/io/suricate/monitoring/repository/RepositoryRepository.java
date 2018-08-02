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

package io.suricate.monitoring.repository;

import io.suricate.monitoring.model.entity.widget.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository used to manage Répository Data from DB
 */
public interface RepositoryRepository extends JpaRepository<Repository, Long> {

    /**
     * Find every repositories order by name
     *
     * @return The list of repositories order by name
     */
    Optional<List<Repository>> findAllByOrderByName();

    /**
     * Find All by enabled order by name
     *
     * @param enabled True if the we want every enabled repository, false otherwise
     * @return The list of repositories
     */
    Optional<List<Repository>> findAllByEnabledOrderByName(final boolean enabled);
}
