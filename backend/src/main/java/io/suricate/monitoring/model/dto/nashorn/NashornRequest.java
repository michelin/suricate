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

import io.suricate.monitoring.model.WidgetState;
import io.suricate.monitoring.model.dto.AbstractDto;
import io.suricate.monitoring.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

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
     * Default constructor
     */
    public NashornRequest() {
        // Empty constructor
    }

    /**
     * Method sued to valid all bean data
     * @return true if all data are valid, false otherwise
     */
    public boolean valid(){
        return projectId != null
                && projectWidgetId != null
                && JsonUtils.isJsonValid(previousData)
                && StringUtils.isNotEmpty(script)
                && delay != null;
    }

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getPreviousData() {
        return previousData;
    }

    public void setPreviousData(String previousData) {
        this.previousData = previousData;
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }

    public NashornRequest(String properties, String script, String previousData, Long projectId, Long technicalId, Long delay, Long timeout, WidgetState state, Date lastSuccess) {
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

    public Long getProjectWidgetId() {
        return projectWidgetId;
    }

    public void setProjectWidgetId(Long projectWidgetId) {
        this.projectWidgetId = projectWidgetId;
    }

    public WidgetState getWidgetState() {
        return widgetState;
    }

    public void setWidgetState(WidgetState widgetState) {
        this.widgetState = widgetState;
    }

    public boolean isAlreadySuccess() {
        return alreadySuccess;
    }

    public void setAlreadySuccess(boolean alreadySuccess) {
        this.alreadySuccess = alreadySuccess;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }
}
