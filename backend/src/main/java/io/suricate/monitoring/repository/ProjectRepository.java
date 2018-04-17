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

import io.suricate.monitoring.model.entity.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository used for request Projects in database
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {

	/**
	 * Find projects by user id
	 *
	 * @param id The user id
	 * @return List of related projects ordered by name
	 */
	List<Project> findByUsers_IdOrderByName(Long id);

	/**
	 * Method used to get Project token from it's id
	 *
	 * @param id the project id
	 * @return the project token
	 */
	@Query("SELECT token FROM Project WHERE id=:id")
	String getToken(@Param("id") Long id);
}
