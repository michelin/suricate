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

package io.suricate.monitoring.model.dto.widget;

import io.suricate.monitoring.model.entity.Asset;
import io.suricate.monitoring.model.entity.widget.Category;
import lombok.*;


/**
 * Represent a cateogry used for communication with the clients via webservices
 */
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode @ToString
public class CategoryDto {
    /**
     * The category id
     */
    private Long id;
    /**
     * The category name
     */
    private String name;
    /**
     * The technical name of the category
     */
    private String technicalName;
    /**
     * The image related to this category
     */
    private Asset image;

    /**
     * Tranform a model object into a dto object
     *
     * @param category The category to transform
     */
    public CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.technicalName = category.getTechnicalName();
        this.image = category.getImage();
    }
}
