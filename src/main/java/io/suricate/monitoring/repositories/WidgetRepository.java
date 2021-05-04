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

import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.model.entities.Widget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository used for request Widget repository in database
 */
public interface WidgetRepository extends JpaRepository<Widget, Long> {


    /**
     * Find all paginated widgets
     *
     * @param specification The specification to apply
     * @param pageable The pageable to apply
     * @return The paginated widgets
     */
    @EntityGraph(attributePaths = {"category", "widgetParams.possibleValuesMap"})
    Page<Widget> findAll(Specification<Widget> specification, Pageable pageable);

    /**
     * Find a widget by id
     *
     * @param id The id
     * @return The widget
     */
    @EntityGraph(attributePaths = {"category.configurations", "widgetParams.possibleValuesMap"})
    Optional<Widget> findById(Long id);

    /**
     * Find a widget by technical name
     *
     * @param technicalName The technical name
     * @return The widget
     */
    Optional<Widget> findByTechnicalName(String technicalName);

    /**
     * Find every widgets by category id
     *
     * @param categoryId The category id
     * @return The list of related widgets ordered by name
     */
    @EntityGraph(attributePaths = {"category.configurations", "widgetParams.possibleValuesMap"})
    List<Widget> findAllByCategoryIdOrderByNameAsc(final Long categoryId);
}
