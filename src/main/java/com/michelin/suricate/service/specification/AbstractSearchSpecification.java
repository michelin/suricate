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

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.domain.Specification;

/**
 * Abstract search specification.
 *
 * @param <T> The type of the specification
 */
@Getter
public abstract class AbstractSearchSpecification<T> implements Specification<T> {
    protected static final String LIKE_OPERATOR_FORMATTER = "%%%s%%";
    protected final String search;
    private final List<String> attributes;

    /**
     * Constructor.
     *
     * @param search           The search query
     * @param filterAttributes The attribute used to filter on search attribute
     */
    protected AbstractSearchSpecification(final String search, final SingularAttribute<T, String>... filterAttributes) {
        this.search = search;
        this.attributes = Arrays.stream(filterAttributes).map(Attribute::getName).toList();
    }

    /**
     * Used to add search predicates.
     *
     * @param root            The root entity
     * @param criteriaBuilder Used to build new predicate
     * @param predicates      The list of predicates to add for this entity
     */
    protected void addSearchPredicate(Root<T> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (StringUtils.isNotBlank(search)) {
            String likeSearchString = String.format(LIKE_OPERATOR_FORMATTER, search.toLowerCase());
            Optional
                .ofNullable(attributes)
                .orElseGet(ArrayList::new)
                .forEach((String attribute) -> predicates.add(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get(attribute)), likeSearchString)));
        }
    }

    /**
     * Used to add predicates to the search query.
     *
     * @param root            The root entity
     * @param criteriaQuery   Used to build queries
     * @param criteriaBuilder Used to build new predicate
     */
    @Override
    public Predicate toPredicate(@NotNull Root<T> root, @NotNull CriteriaQuery<?> criteriaQuery,
                                 @NotNull CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        addSearchPredicate(root, criteriaBuilder, predicates);

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
