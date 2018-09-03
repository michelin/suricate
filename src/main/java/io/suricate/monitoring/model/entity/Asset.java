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

package io.suricate.monitoring.model.entity;


import lombok.*;

import javax.persistence.*;

/**
 * Library entity
 */
@Entity
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(callSuper = false) @ToString
public class Asset extends AbstractAuditingEntity<Long> {

    /**
     * The id
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The content of this asset
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "LONGBLOB")
    private byte[] content;

    /**
     * The content type
     */
    @Column(length = 100)
    private String contentType;

    /**
     * The size
     */
    @Column
    private long size;


    public void setContent(byte[] content) {
        this.content = content;
        this.size = content.length;
    }
}
