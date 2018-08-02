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

import io.suricate.monitoring.model.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository used for request Users in database
 */
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * Find every user by username
	 *
	 * @return The list of users by username
	 */
	Optional<List<User>> findAllByOrderByUsername();

	/**
	 * Find a user by the username without taking case into account
	 *
	 * @param username The username
	 * @return The user as optional
	 */
	Optional<User> findByUsernameIgnoreCase(String username);

	/**
	 * Search users with username starting by the username in params
	 *
	 * @param username The part of the username to search
	 * @return The list of related users
	 */
	@Query("SELECT u FROM User u " +
			"WHERE lower(u.username) LIKE lower(concat(:username, '%'))")
	Optional<List<User>> findByUsernameIgnoreCaseAndStartingWith(@Param("username") String username);

	/**
	 * Methos used to get id by username
	 * @param username the user name to find
	 * @return the user id
	 */
	@Query("SELECT id FROM User WHERE username = :username")
    Long getIdByUsername(@Param("username") String username);

	/**
	 * Method used tofins all user by project id
	 * @param id the project id
	 * @return the list of user
	 */
	Optional<List<User>> findByProjects_Id(Long id);
}
