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

import javax.persistence.*;

@Entity(name = "user_setting")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserSetting extends AbstractEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unconstrained_value")
    private String unconstrainedValue;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "setting_id", nullable = false)
    private Setting setting;

    @ToString.Exclude
    @OneToOne
    @JoinColumn(name = "allowed_setting_value_id")
    private AllowedSettingValue settingValue;

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
