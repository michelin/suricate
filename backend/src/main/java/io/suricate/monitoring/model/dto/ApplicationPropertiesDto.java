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

package io.suricate.monitoring.model.dto;

import lombok.*;

/**
 * Send the application properties needed by the frontend
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class ApplicationPropertiesDto extends AbstractDto {
    private String key;
    private String value;
    private String description;

    /**
     * Args constructor
     *
     * @param key         The key
     * @param value       The value
     * @param description The description
     */
    public ApplicationPropertiesDto(final String key, final String value, final String description) {
        this.key = key;
        this.value = value;
        this.description = description;
    }
}
