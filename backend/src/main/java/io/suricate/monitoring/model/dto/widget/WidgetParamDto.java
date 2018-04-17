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

import io.suricate.monitoring.model.enums.WidgetVariableType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a widget param response used for communication with the clients via webservices
 */
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode @ToString
public class WidgetParamDto {
    /**
     * The param name
     */
    private String name;

    /**
     * The param description
     */
    private String description;

    /**
     * The default value of the param
     */
    private String defaultValue;

    /**
     * The param type {@link WidgetVariableType}
     */
    private WidgetVariableType type;

    /**
     * The regex used for accept a file while uploading it if the type is a FILE
     */
    private String acceptFileRegex;

    /**
     * An exemple of the usage of this param
     */
    private String usageExample;

    /**
     * If the param is required True by default
     */
    private boolean required = true;

    /**
     * The list of param values if the type is COMBO or a MULTIPLE
     */
    private List<WidgetParamValueDto> values = new ArrayList<>();
}
