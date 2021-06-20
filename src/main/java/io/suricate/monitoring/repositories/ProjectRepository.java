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

package io.suricate.monitoring.repositories;

import io.suricate.monitoring.model.entities.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository used for request Projects in database
 */
@Repository
public interface ProjectRepository extends CrudRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    /**
     * Find projects by user id
     *
     * @param id The user id
     * @return List of related projects ordered by name
     */
    List<Project> findByUsersIdOrderByName(Long id);

    /**
	 * Find a project by token
	 *
	 * @param token The token to find
	 * @return The project as Optionals
	 */
    @EntityGraph(attributePaths = {"screenshot", "widgets", "users"})
	Optional<Project> findProjectByToken(final String token);

	/**
	 * Method used to get Project token from it's id
	 *
	 * @param id the project id
	 * @return the project token
	 */
	@Query("SELECT token FROM Project WHERE id=:id")
	String getToken(@Param("id") Long id);
}
