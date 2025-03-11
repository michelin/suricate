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
package com.michelin.suricate.service.specification;

import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.model.entity.Project_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/** Project search specification. */
public class ProjectSearchSpecification extends AbstractSearchSpecification<Project> {
    public ProjectSearchSpecification(final String search) {
        super(search);
    }

    /**
     * Used to add search predicates.
     *
     * @param root The root entity
     * @param criteriaBuilder Used to build new predicate
     * @param predicates The list of predicates to add for this entity
     */
    @Override
    protected void addSearchPredicate(Root<Project> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        // Add a predicate for each word in the research string.
        // Allow the user to retrieve projects with multiple consecutive whitespaces
        // in the title even by typing a single whitespace
        if (StringUtils.isNotBlank(search)) {
            for (String keyWord : search.split("\\s+")) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(Project_.name)),
                        String.format(LIKE_OPERATOR_FORMATTER, keyWord.toLowerCase())));
            }
        }
    }
}
