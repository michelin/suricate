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

import io.suricate.monitoring.model.entities.Widget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * Repository used for request Widget repository in database
 */
public interface WidgetRepository extends JpaRepository<Widget, Long>, JpaSpecificationExecutor<Widget> {

    /**
     * Find a widget by technical name
     *
     * @param technicalname The technical name
     * @return The related widget
     */
    Widget findByTechnicalName(String technicalname);

    /**
     * Find every widgets by category id
     *
     * @param categoryId The category id
     * @return The list of related widgets ordered by name
     */
    List<Widget> findAllByCategory_IdOrderByNameAsc(final Long categoryId);

}
