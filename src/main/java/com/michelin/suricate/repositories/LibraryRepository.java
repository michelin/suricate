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

package com.michelin.suricate.repositories;

import com.michelin.suricate.model.entities.Library;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Library repository.
 */
@Repository
public interface LibraryRepository extends JpaRepository<Library, Long>, JpaSpecificationExecutor<Library> {
    /**
     * Find all libraries by given widget ids.
     *
     * @param widgetIds The widget ids
     * @return The libraries
     */
    @Cacheable("lib-by-widget-id")
    List<Library> findDistinctByWidgetsIdIn(List<Long> widgetIds);

    /**
     * Find a library by technical name.
     *
     * @param technicalName The technical name
     * @return The library
     */
    Library findByTechnicalName(String technicalName);
}
