/*
 * Copyright 2012-2021 the original author or authors.
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

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * Representation of websocket client
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@Schema(name = "WebsocketClient", description = "Define a connected screen to websocket")
public class WebsocketClient extends AbstractDto {
    @Schema(name = "The connected project token")
    private String projectToken;

    @Schema(name = "The websocket session id")
    private String sessionId;

    @Schema(name = "The subscription id related to unique screen WS subscription")
    private String subscriptionId;

    @Schema(name = "Screen reference")
    private String screenCode;
}
