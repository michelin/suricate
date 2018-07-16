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
import io.suricate.monitoring.model.dto.user.UserDto;
import io.suricate.monitoring.model.dto.websocket.WebsocketClient;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Project object used for communication with clients of the webservice
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@ApiModel(value = "Project", description = "Describe a project/dashboard")
public class ProjectDto extends AbstractDto {

    /**
     * The id
     */
    @ApiModelProperty(value = "The project id", required = true)
    private Long id;
    /**
     * The project name
     */
    @ApiModelProperty(value = "The project name", required = true)
    private String name;
    /**
     * Number of column in the dashboard
     */
    @ApiModelProperty(value = "The number of columns in the dashboard")
    private Integer maxColumn;
    /**
     * The height for widgets contained
     */
    @ApiModelProperty(value = "The height in pixel of the widget")
    private Integer widgetHeight;
    /**
     * The global css for the dashboard
     */
    @ApiModelProperty(value = "The css style of the dashboard grid")
    private String cssStyle;
    /**
     * The dashboard token
     */
    @ApiModelProperty(value = "The project token", required = true)
    private String token;
    /**
     * The list of widgets
     */
    @ApiModelProperty(value = "The list of related instantiate widgets", dataType = "List")
    private List<ProjectWidgetDto> projectWidgets = new ArrayList<>();
    /**
     * The librairies related
     */
    @ApiModelProperty(value = "The list of the related JS libraries used for the execution of the widgets", dataType = "List")
    private List<String> librariesToken = new ArrayList<>();
    /**
     * The users added to the widget
     */
    @ApiModelProperty(value = "The list of users of the dashboard", dataType = "List")
    private List<UserDto> users = new ArrayList<>();

    /**
     * The list of every connected clients through web socket
     */
    @ApiModelProperty(value = "The list of the current connected screens", dataType = "List")
    private List<WebsocketClient> websocketClients = new ArrayList<>();
}
