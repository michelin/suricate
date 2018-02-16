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

import io.suricate.monitoring.model.entity.widget.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Method used to find category by technical name
     * @param technicalName category technical name
     * @return the category found or null otherwise
     */
    Category findByTechnicalName(String technicalName);

    /**
     * Method used to get all categorgy ordered by name
     * @return
     */
    List<Category> findAllByOrderByNameAsc();
}
