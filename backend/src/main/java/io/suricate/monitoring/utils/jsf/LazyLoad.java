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

package io.suricate.monitoring.utils.jsf;

import io.suricate.monitoring.utils.SecurityUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.Id;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LazyLoad<T, ID extends Serializable> extends LazyDataModel<T> {

    private static final Logger LOG = LoggerFactory.getLogger(LazyLoad.class);
    protected transient List<T> list;
    protected long totalElements;

    protected transient Map<String, Object> filters;

    protected transient JpaSpecificationExecutor<T> service;


    /**
     * Constructor
     *
     * @param service
     */
    public LazyLoad(JpaSpecificationExecutor<T> service) {
        this.service = service;
    }

    /**
     * Constructor
     *
     * @param service
     */
    public LazyLoad(JpaSpecificationExecutor<T> service, Map<String, Object> filters) {
        this.service = service;
        this.filters = filters;
    }

    @Override
    public T getRowData(String id) {
        if (list != null) {
            for (T instance : list) {
                Object instanceId = getIdOfInstance(instance);
                if (instanceId != null && id.equals(instanceId.toString())) {
                    return instance;
                }
            }
        }

        return null;
    }

    private static Object getIdOfInstance(Object instance) {
        Object instanceId = null;
        try {
            Class instanceClass = instance.getClass();
            Field[] fields = FieldUtils.getFieldsWithAnnotation(instance.getClass(),Id.class);
            if (fields != null && fields.length > 0){
                String idName = fields[0].getName();
                idName = idName.substring(0, 1).toUpperCase() + idName.substring(1);
                Method method = MethodUtils.getAccessibleMethod(instanceClass,"get" + idName);
                instanceId = method.invoke(instance);
            }
        } catch (Exception ex) {
            LOG.warn(null, ex);
        }

        return instanceId;
    }

    @Override
    public Object getRowKey(T instance) {
        return getIdOfInstance(instance);
    }

    @Override
    public void setRowIndex(final int rowIndex) {
        if (rowIndex == -1 || getPageSize() == 0) {
            super.setRowIndex(-1);
        } else {
            super.setRowIndex(rowIndex % getPageSize());
        }
    }

    /**
     * callback method
     */
    public Page<T> load(Pageable page, Map<String, Object> pFilters) {
        Map<String, Object> customFilters = pFilters;
        if (!SecurityUtils.isAdmin()) {
            if (customFilters == null) {
                customFilters = new HashMap<>();
            }
            customFilters.putAll(this.filters);
        }

        if (customFilters.isEmpty()) {
            if (service instanceof JpaRepository) {
                return ((JpaRepository) service).findAll(page);
            } else {
                LOG.error("Your repository must extends JpaRepository");
            }
        }
        return service.findAll(FilterMapSpecification.byMap(customFilters), page);
    }

    @Override
    public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        Sort.Direction direction;
        if (sortOrder == SortOrder.ASCENDING) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        Page<T> page = null;
        // SORT
        if (sortField != null) {
            page = load(new PageRequest(first / pageSize, pageSize, direction, sortField.trim()), filters);
        } else {
            page = load(new PageRequest(first / pageSize, pageSize), filters);
        }

        if (page != null) {
            list = page.getContent();
            totalElements = page.getTotalElements();
            this.setRowCount((int) totalElements);
        } else {
            list = new ArrayList<T>();
            totalElements = 0L;
            setRowCount(0);
        }

        return list;
    }

    public List<T> getContents() {
        return list;
    }

    public long getTotalElements() {
        return totalElements;
    }
}
