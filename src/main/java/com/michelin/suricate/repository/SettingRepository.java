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

import com.michelin.suricate.model.entity.Setting;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/** Setting repository. */
@Repository
public interface SettingRepository extends CrudRepository<Setting, Long>, JpaSpecificationExecutor<Setting> {
    /**
     * Find setting by id.
     *
     * @param id The setting id
     * @return The setting
     */
    @NotNull Optional<Setting> findById(@NotNull final Long id);

    /**
     * Find settings by description.
     *
     * @return The list of the settings
     */
    @EntityGraph(attributePaths = "allowedSettingValues")
    Optional<List<Setting>> findAllByOrderByDescription();
}
