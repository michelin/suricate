/*
 * Copyright 2012-2021 the original author or authors.
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

package com.michelin.suricate.model.dto.nashorn;

import com.michelin.suricate.model.dto.api.AbstractDto;
import com.michelin.suricate.model.enums.DataTypeEnum;
import lombok.*;

import java.util.Map;

/**
 * Widget variable used for communication with the clients via webservices
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class WidgetVariableResponse extends AbstractDto {

    /**
     * The variable name
     */
    private String name;

    /**
     * The variable description
     */
    private String description;

    /**
     * The data
     */
    private String data;

    /**
     * The variable type
     */
    private DataTypeEnum type;

    /**
     * If the variable is required
     */
    private boolean required;

    /**
     * The default value
     */
    private String defaultValue;

    /**
     * Map of values
     */
    private Map<String, String> values;
}
