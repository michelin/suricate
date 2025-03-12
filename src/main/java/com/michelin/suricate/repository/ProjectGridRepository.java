/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.michelin.suricate.repository;

import com.michelin.suricate.model.entity.ProjectGrid;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/** Project grid repository. */
@Repository
public interface ProjectGridRepository extends JpaRepository<ProjectGrid, Long>, JpaSpecificationExecutor<ProjectGrid> {
    /**
     * Delete a grid by its id and the project id.
     *
     * @param projectId the project id
     * @param id the widget instance id
     */
    void deleteByProjectIdAndId(Long projectId, Long id);

    /**
     * Find a grid by id and project token.
     *
     * @param id The ID
     * @param token The project token
     * @return The grid
     */
    Optional<ProjectGrid> findByIdAndProjectToken(Long id, String token);
}
