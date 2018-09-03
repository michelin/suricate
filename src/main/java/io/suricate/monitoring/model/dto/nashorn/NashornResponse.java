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
import io.suricate.monitoring.model.enums.NashornErrorTypeEnum;
import io.suricate.monitoring.utils.JsonUtils;
import lombok.*;

import java.util.Date;

/**
 * Represent the response after nashorn execution
 */
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(callSuper = false) @ToString
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
    private NashornErrorTypeEnum error;

    /**
     * Method used to check if the object is isValid
     * @return true if this object is isValid, false otherwise
     */
    public boolean isValid(){
        return JsonUtils.isJsonValid(data) && projectId != null && projectWidgetId != null && error == null;
    }

    public boolean isFatal() {
        return NashornErrorTypeEnum.FATAL == error;
    }
}
