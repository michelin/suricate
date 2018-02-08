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

package io.suricate.monitoring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.suricate.monitoring.utils.ToStringUtils;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Abstract Model
 * @param <ID> Entity id type
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractModel<ID> implements Serializable {

    @Override
    public String toString() {
        return ToStringUtils.toStringEntity(this);
    }

    /**
     * Method used to get an explicit name for an entity
     * @return
     */
    @JsonIgnore
    public abstract String getExplicitName();

    /**
     * Method used to get the Entity ID
     * @return the entity ID
     */
    public abstract ID getId();

    /**
     * Boolean used to define if the entity is already persisted in the database
     */
    @JsonIgnore
    @Transient
    private boolean alreadyPersisted;

    /**
     * Default equals
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractModel model = (AbstractModel) o;

        return getId() != null ? getId().equals(model.getId()) : model.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    public boolean isAlreadyPersisted() {
        return alreadyPersisted;
    }

    public void setAlreadyPersisted(boolean alreadyPersisted) {
        this.alreadyPersisted = alreadyPersisted;
    }

    @PostLoad
    @PostPersist
    /**
     * Method used to indicate if the entity has been persisted
     */
    public void postLoad(){
        alreadyPersisted = true;
    }
}
