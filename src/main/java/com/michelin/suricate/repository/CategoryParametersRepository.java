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

import com.michelin.suricate.model.entity.CategoryParameter;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/** Category parameters repository. */
@Repository
public interface CategoryParametersRepository
        extends JpaRepository<CategoryParameter, String>, JpaSpecificationExecutor<CategoryParameter> {

    /**
     * Get the list of category parameters for a category ID.
     *
     * @param categoryId The category ID
     * @return The list of category parameters
     */
    Optional<List<CategoryParameter>> findCategoryParametersByCategoryId(Long categoryId);
}
