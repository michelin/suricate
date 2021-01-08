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

import io.suricate.monitoring.model.entities.WidgetConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * Repository used for request Configurations in database
 */
public interface WidgetConfigurationRepository extends JpaRepository<WidgetConfiguration, String>, JpaSpecificationExecutor<WidgetConfiguration> {

    /**
     * Get the list of configurations for a category
     *
     * @param categoryId The category to find
     * @return The list of related configurations
     */
    Optional<List<WidgetConfiguration>> findConfigurationByCategoryId(Long categoryId);

}
