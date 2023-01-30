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

import com.michelin.suricate.model.entities.Category;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository used for request categories in database
 */
@Repository
public interface CategoryRepository extends CrudRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    /**
     * Find all paginated categories
     *
     * @param specification The specification to apply
     * @param pageable The pageable to apply
     * @return The paginated categories
     */
    @NotNull
    @EntityGraph(attributePaths = {"configurations", "image", "widgets", "widgets.repository", "widgets.image", "widgets.libraries", "widgets.widgetParams", "widgets.widgetParams.possibleValuesMap"})
    Page<Category> findAll(Specification<Category> specification, @NotNull Pageable pageable);

    /**
     * Find category by technical name
     *
     * @param technicalName The technical name
     * @return The category
     */
    Category findByTechnicalName(String technicalName);
}
