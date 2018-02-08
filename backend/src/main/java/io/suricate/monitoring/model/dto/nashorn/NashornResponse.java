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

import io.suricate.monitoring.model.dto.AbstractDto;
import io.suricate.monitoring.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class NashornResponse extends AbstractDto {

    /**
     * The new calculated data
     */
    private String data;

    /**
     * the log data
     */
    private String log;

    /**
     * The project Id
     */
    private Long projectId;

    /**
     * The projectwidget Id
     */
    private Long projectWidgetId;

    /**
     * Launch date
     */
    private Date launchDate;

    /**
     * Error
     */
    private ErrorType error;

    /**
     * Method used to check if the object is valid
     * @return true if this object is valid, false otherwise
     */
    public boolean isValid(){
        return JsonUtils.isJsonValid(data) && projectId != null && projectWidgetId != null && error == null;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = StringUtils.trimToNull(log);
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Date getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(Date launchDate) {
        this.launchDate = launchDate;
    }

    public boolean isFatal() {
        return ErrorType.FATAL == error;
    }

    public void setError(ErrorType error) {
        this.error = error;
    }

    public Long getProjectWidgetId() {
        return projectWidgetId;
    }

    public void setProjectWidgetId(Long projectWidgetId) {
        this.projectWidgetId = projectWidgetId;
    }

    public ErrorType getError() {
        return error;
    }
}
