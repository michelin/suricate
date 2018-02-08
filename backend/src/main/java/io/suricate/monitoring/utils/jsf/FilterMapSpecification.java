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

package io.suricate.monitoring.utils.jsf;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class FilterMapSpecification<T> implements Specification<T> {

    private Map<String, Object> filter;

    private FilterMapSpecification(Map<String, Object> filter) {
        this.filter = filter;
    }

    public static <T> FilterMapSpecification<T> byMap(Map<String, Object> filter) {
        return new FilterMapSpecification<T>(filter);
    }

    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();

        // Query by map filter
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            String key = entry.getKey().trim();
            Path<?> realPath = root;
            String[] split = key.split("\\.");
            for(int i = 0;i < split.length -1; i++){
                realPath = root.join(split[i]);
            }
            key = split[split.length - 1];
            if (realPath.get(key) == null) {
                throw new RuntimeException("Invalid filter mapping, path: " + key);
            }

            Predicate predicate = buildPredicate(realPath, builder, null, key, entry.getValue());
            if (predicate != null) {
                predicates.add(predicate);
            }
        }

        if (!predicates.isEmpty()) {
            return builder.and(predicates.toArray(new Predicate[predicates.size()]));
        }

        return null;
    }

    /**
     * Method used to build predicate
     * @param root
     * @param builder
     * @param type
     * @param key
     * @param value
     * @return
     */
    private static Predicate buildPredicate(Path<?> root, CriteriaBuilder builder, Class<?> type, String key, Object value) {
        if (value == null) {
            return null;
        }

        Class<?> realType = type;
        if (realType == null) {
            realType = value.getClass();
        }

        if (realType == String.class && value.toString().trim().length() == 0) {
            return null;
        }

        if (value instanceof String) {
            // use || to search multiple value
            if (((String) value).contains("||")) {
                String[] array = ((String) value).split("\\|\\|");
                List<Predicate> disjunction = new ArrayList<>();
                for (String val : array){
                    disjunction.add(builder.like(builder.upper(root.get(key).as(String.class)), "%" + val.toUpperCase() + "%"));
                }
                return builder.or(disjunction.toArray(new Predicate[array.length]));
            }
            return builder.like(builder.upper(root.get(key).as(String.class)), "%" + ((String) value).toUpperCase() + "%");
        }

        // Filter with list
        if (value instanceof List){
            CriteriaBuilder.In<String> in = builder.in(root.get(key).as(String.class));
            for (Object obj : (List)value){
                in.value(obj.toString());
            }
            return in;
        }

        return builder.equal(root.get(key).as(realType), value);
    }
}
