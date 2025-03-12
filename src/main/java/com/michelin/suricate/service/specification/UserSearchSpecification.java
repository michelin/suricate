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

import com.michelin.suricate.model.entity.User;
import com.michelin.suricate.model.entity.User_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/** User search specification. */
public class UserSearchSpecification extends AbstractSearchSpecification<User> {
    /**
     * Constructor.
     *
     * @param search The string to search
     */
    public UserSearchSpecification(final String search) {
        super(search);
    }

    /**
     * Used to add search predicates. Add research by username, firstname or lastname. Override the default
     * addSearchPredicate which performs "and" operator instead of "or".
     *
     * @param root The root entity
     * @param criteriaBuilder Used to build new predicate
     * @param predicates The list of predicates to add for this entity
     */
    @Override
    protected void addSearchPredicate(Root<User> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (StringUtils.isNotBlank(search)) {
            Predicate searchByUsername = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(User_.username)),
                    String.format(LIKE_OPERATOR_FORMATTER, search.toLowerCase()));
            Predicate searchByFirstname = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(User_.firstname)),
                    String.format(LIKE_OPERATOR_FORMATTER, search.toLowerCase()));
            Predicate searchByLastname = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(User_.lastname)),
                    String.format(LIKE_OPERATOR_FORMATTER, search.toLowerCase()));

            predicates.add(criteriaBuilder.or(searchByUsername, searchByFirstname, searchByLastname));
        }
    }
}
