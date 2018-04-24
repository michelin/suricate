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
 * Asset class used for communicate through webservices
 */
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(callSuper = false) @ToString
public class AssetDto extends AbstractDto  {
    /**
     * The asset id
     */
    private Long id;

    /**
     * The blob content
     */
    private byte[] content;

    /**
     * The content type
     */
    private String contentType;

    /**
     * The size of the asset
     */
    private long size;
}
