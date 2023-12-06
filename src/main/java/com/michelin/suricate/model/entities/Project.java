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
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.validation.constraints.NotBlank;
import java.sql.Types;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.YesNoConverter;

/**
 * Project entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class Project extends AbstractAuditingEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String token;

    @Column
    private Integer widgetHeight;

    @Column
    private Integer maxColumn;

    @Lob
    @JdbcTypeCode(Types.LONGNVARCHAR)
    private String cssStyle;

    @Column(nullable = false)
    @Convert(converter = YesNoConverter.class)
    private boolean displayProgressBar;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "screenshot_id")
    private Asset screenshot;

    @ToString.Exclude
    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    @OrderBy("id ASC")
    private Set<ProjectGrid> grids = new LinkedHashSet<>();

    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "user_project", joinColumns = {@JoinColumn(name = "project_id")}, inverseJoinColumns = {
        @JoinColumn(name = "user_id")})
    private Set<User> users = new LinkedHashSet<>();

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
