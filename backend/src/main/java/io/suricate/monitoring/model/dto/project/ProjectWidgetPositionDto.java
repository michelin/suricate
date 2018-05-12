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

package io.suricate.monitoring.model.dto.project;

import io.suricate.monitoring.model.dto.AbstractDto;
import lombok.*;

/**
 * Widget position used for communication with the clients via webservices
 * (For example when a widget change of position)
 */
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(callSuper = false) @ToString
public class ProjectWidgetPositionDto extends AbstractDto {

    /**
     * The project widget id related to this project widget position
     */
    private Long projectWidgetId;

    /**
     * The start column of this widget
     */
    private int col;

    /**
     * The start row of the widget
     */
    private int row;

    /**
     * The number of columns for this widget
     */
    private int width;

    /**
     * The number of rows
     */
    private int height;
}
