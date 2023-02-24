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

import com.michelin.suricate.model.entities.generic.AbstractEntity;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity(name = "allowed_setting_value")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AllowedSettingValue extends AbstractEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    @Type(type = "yes_no")
    private boolean isDefault;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "setting_id", referencedColumnName = "id", nullable = false)
    private Setting setting;

    /**
     * Hashcode method
     * Do not used lombok @EqualsAndHashCode method as it calls super method
     * then call the self-defined child Hashcode method
     * @return The hash code
     */
    @Override
    public int hashCode() { return super.hashCode(); }

    /**
     * Equals method
     * Do not used lombok @EqualsAndHashCode method as it calls super method
     * then call the self-defined child Equals method
     * @param other The other object to compare
     * @return true if equals, false otherwise
     */
    @Override
    public boolean equals(Object other) { return super.equals(other); }
}
