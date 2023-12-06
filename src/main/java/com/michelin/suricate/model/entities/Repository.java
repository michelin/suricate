/*
 *
 *  * Copyright 2012-2021 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.michelin.suricate.model.entities;

import com.michelin.suricate.model.entities.generic.AbstractAuditingEntity;
import com.michelin.suricate.model.enums.RepositoryTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.type.YesNoConverter;

/**
 * Repository entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class Repository extends AbstractAuditingEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column
    private String url;

    @Column
    private String branch;

    @Column
    private String login;

    @Column
    private String password;

    @Column
    private String localPath;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RepositoryTypeEnum type;

    @Column(nullable = false)
    @Convert(converter = YesNoConverter.class)
    private boolean enabled = true;

    @Column(nullable = false)
    private int priority;

    @ToString.Exclude
    @OneToMany(mappedBy = "repository")
    private Set<Widget> widgets = new LinkedHashSet<>();

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
