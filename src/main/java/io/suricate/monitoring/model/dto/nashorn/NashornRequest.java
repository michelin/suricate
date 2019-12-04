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

package io.suricate.monitoring.model.dto.nashorn;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.suricate.monitoring.model.enums.WidgetState;
import io.suricate.monitoring.utils.JsonUtils;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * Represent a nashorn request (used for execute the widget JS, with nashorn)
 */
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(callSuper = false) @ToString
public class NashornRequest extends AbstractDto {


    /**
     * Nashorn Properties
     */
    private String properties;

    /**
     * javascript script
     */
    private String script;

    /**
     * Previous json data
     */
    private String previousData;

    /**
     * project id
     */
    private Long projectId;


    /**
     * Project widget ID
     */
    private Long projectWidgetId;

    /**
     * The delay to launch the script
     */
    private Long delay;

    /**
     * Widget State
     */
    private WidgetState widgetState;

    /**
     * Boolean to indicate if the
     */
    private boolean alreadySuccess;

    /**
     * The override timeout
     */
    private Long timeout;

    /**
     * Full constructor
     *
     * @param properties The project widget backend config
     * @param script The widget js script
     * @param previousData The data of the last execution
     * @param projectId The project id
     * @param technicalId The project widget id
     * @param delay The delay before the next run
     * @param timeout The timeout before interruption of the run
     * @param state The project widget state
     * @param lastSuccess The last success date
     */
    public NashornRequest(
        String properties,
        String script,
        String previousData,
        Long projectId,
        Long technicalId,
        Long delay,
        Long timeout,
        WidgetState state,
        Date lastSuccess
    ) {
        this.properties = properties;
        this.script = script;
        this.previousData = previousData;
        this.projectId = projectId;
        this.projectWidgetId = technicalId;
        this.delay = delay;
        this.widgetState = state;
        this.timeout = timeout;
        this.alreadySuccess = lastSuccess != null;
    }

    /**
     * Method used to validate all bean data
     * @return true if all data are Valid, false otherwise
     */
    public boolean isValid(){
        return projectId != null
                && projectWidgetId != null
                && JsonUtils.isJsonValid(previousData)
                && StringUtils.isNotEmpty(script)
                && delay != null;
    }
}
