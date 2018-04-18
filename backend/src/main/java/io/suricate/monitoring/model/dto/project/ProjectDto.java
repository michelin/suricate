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

import io.suricate.monitoring.model.dto.user.UserDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Project object used for communication with clients of the webservice
 */
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode @ToString
public class ProjectDto {

    /**
     * The id
     */
    private Long id;
    /**
     * The project name
     */
    private String name;
    /**
     * Number of column in the dashboard
     */
    private Integer maxColumn;
    /**
     * The height for widgets contained
     */
    private Integer widgetHeight;
    /**
     * The global css for the dashboard
     */
    private String cssStyle;
    /**
     * The dashboard token
     */
    private String token;
    /**
     * The list of widgets
     */
    private List<ProjectWidgetDto> projectWidgets = new ArrayList<>();
    /**
     * The librairies related
     */
    private List<String> librariesToken = new ArrayList<>();
    /**
     * The users added to the widget
     */
    private List<UserDto> users = new ArrayList<>();
}
