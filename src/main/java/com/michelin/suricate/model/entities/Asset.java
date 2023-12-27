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

package com.michelin.suricate.model.entities;


import com.michelin.suricate.model.entities.generic.AbstractAuditingEntity;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Asset entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class Asset extends AbstractAuditingEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] content;

    @Column(length = 100)
    private String contentType;

    @Column
    private long size;

    public void setContent(byte[] content) {
        this.content = content;
        this.size = content.length;
    }

    /**
     * Hashcode method.
     * Do not use lombok @EqualsAndHashCode method as it calls super method
     * then call the self-defined child Hashcode method
     *
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Equals method.
     * Do not use lombok @EqualsAndHashCode method as it calls super method
     * then call the self-defined child Equals method
     *
     * @param other The other object to compare
     * @return true if equals, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        return super.equals(other);
    }
}
