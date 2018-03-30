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

package io.suricate.monitoring.model.dto.websocket;

import io.suricate.monitoring.model.dto.AbstractDto;
import lombok.*;

/**
 * Representation of websocket client
 */
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(callSuper = false) @ToString
public class WebsocketClient extends AbstractDto {

    /**
     * The project id subscribed by the client
     */
    private String projectId;

    /**
     * The websocket session id
     */
    private String sessionId;

    /**
     * The client screen code
     */
    private String screenCode;

    /**
     * Default constructor using fields
     * @param projectId the project id
     * @param sessionId the session id
     * @param screenCode The screenCode
     */
    public WebsocketClient(String projectId, String sessionId, String screenCode) {
        this.projectId = projectId;
        this.sessionId = sessionId;
        this.screenCode = screenCode;
    }
}
