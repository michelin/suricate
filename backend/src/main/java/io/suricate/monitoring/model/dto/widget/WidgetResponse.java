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
import io.suricate.monitoring.model.dto.AbstractDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The widget response used for communication with the clients via webservices
 */
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(callSuper = false) @ToString
public class WidgetResponse extends AbstractDto {

    /**
     * The widget id
     */
    private String id;

    /**
     * The name of the widget
     */
    private String name;

    /**
     * The html content after instantiation by nashorn
     */
    private String html;

    /**
     * The css of the widget (defined in the css file)
     */
    private String css;

    /**
     * The custom css (override by the user)
     */
    private String customCss;

    /**
     * The start column of the widget
     */
    private int col;

    /**
     * The start row of the widget
     */
    private int row;

    /**
     * The width of the widget (number of row taken in the grid)
     */
    private int width;

    /**
     * The height of the widget (number of height taken in the grid)
     */
    private int height;

    /**
     * The project widget id
     */
    private Long projectWidgetId;

    /**
     * The widget id
     */
    private Long widgetId;

    /**
     * If the widget is in error
     */
    private boolean error;

    /**
     * The widget image
     */
    private Asset image;

    /**
     * The image id
     */
    private Long imageId;

    /**
     * If the widget got warnings
     */
    private boolean warning;

    /**
     * The description of the widget
     */
    private String description;

    /**
     * Informations on the widget
     */
    private String info;

    /**
     * The list of params
     */
    private List<WidgetParamResponse> widgetParams = new ArrayList<>();
}
