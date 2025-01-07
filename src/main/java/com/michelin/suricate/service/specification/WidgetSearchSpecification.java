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

import com.michelin.suricate.model.entity.Category;
import com.michelin.suricate.model.entity.Category_;
import com.michelin.suricate.model.entity.Widget;
import com.michelin.suricate.model.entity.Widget_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * Widget search specification.
 */
public class WidgetSearchSpecification extends AbstractSearchSpecification<Widget> {
    /**
     * Constructor.
     *
     * @param search The string to search
     */
    public WidgetSearchSpecification(final String search) {
        super(search);
    }

    /**
     * Used to add search predicates.
     *
     * @param root            The root entity
     * @param criteriaBuilder Used to build new predicate
     * @param predicates      The list of predicates to add for this entity
     */
    @Override
    protected void addSearchPredicate(Root<Widget> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (StringUtils.isNotBlank(search)) {
            Predicate searchByName = criteriaBuilder.like(criteriaBuilder.lower(root.get(Widget_.name)),
                String.format(LIKE_OPERATOR_FORMATTER, search.toLowerCase()));
            Predicate searchByCategoryName = widgetByCategoryName(root, criteriaBuilder);

            predicates.add(criteriaBuilder.or(searchByName, searchByCategoryName));
        }
    }

    /**
     * Add a predicate which allow widgets to be searched by the category name.
     *
     * @param root            The root entity
     * @param criteriaBuilder The criteria builder
     * @return A predicate on the category name
     */
    private Predicate widgetByCategoryName(Root<Widget> root, CriteriaBuilder criteriaBuilder) {
        Join<Widget, Category> join = root.join(Widget_.category);

        return criteriaBuilder.like(
            criteriaBuilder.lower(join.get(Category_.name)),
            String.format(LIKE_OPERATOR_FORMATTER, search.toLowerCase()));
    }
}
