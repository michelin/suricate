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

public class WebsocketClient extends AbstractDto {

    private String projectId;

    private String sessionId;

    private String id;

    /**
     * Default constructor using fields
     * @param projectId the project id
     * @param sessionId the session id
     * @param id client id
     */
    public WebsocketClient(String projectId, String sessionId, String id) {
        this.projectId = projectId;
        this.sessionId = sessionId;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }

        WebsocketClient websocketClient = (WebsocketClient) o;

        return getSessionId() == null ? websocketClient.getSessionId() == null : getSessionId().equals(websocketClient.getSessionId());
    }

    @Override
    public int hashCode() {
        return getSessionId() == null ? 0 : getSessionId().hashCode();
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
