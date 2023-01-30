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

import com.michelin.suricate.model.entities.CategoryParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository used for request Configurations in database
 */
@Repository
public interface CategoryParametersRepository extends JpaRepository<CategoryParameter, String>, JpaSpecificationExecutor<CategoryParameter> {

    /**
     * Get the list of category parameters for a category ID
     *
     * @param categoryId The category ID
     * @return The list of category parameters
     */
    Optional<List<CategoryParameter>> findCategoryParametersByCategoryId(Long categoryId);
}
