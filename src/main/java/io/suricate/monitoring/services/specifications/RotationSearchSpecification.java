/*
 *
 *  * Copyright 2012-2018 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.suricate.monitoring.services.specifications;

import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.Project_;
import io.suricate.monitoring.model.entities.Rotation;
import io.suricate.monitoring.model.entities.Rotation_;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;
import java.util.List;

public class RotationSearchSpecification extends AbstractSearchSpecification<Rotation> {
    /**
     * Constructor
     *
     * @param search The search query
     */
    public RotationSearchSpecification(String search) {
        super(search);
    }

    /**
     * Used to add search predicates
     *
     * @param root            The root entity
     * @param criteriaBuilder Used to build new predicate
     * @param predicates      The list of predicates to add for this entity
     */
    @Override
    protected void addSearchPredicate(Root<Rotation> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        // Add a predicate for each word in the research string. Allow the user to retrieve rotations with multiple consecutive whitespaces in the title even by typing a single whitespace
        if (StringUtils.isNotBlank(search)) {
            for (String keyWord : search.split("\\s+")) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get(Rotation_.name)),
                        String.format(LIKE_OPERATOR_FORMATTER, keyWord.toLowerCase())));
            }
        }
    }
}
