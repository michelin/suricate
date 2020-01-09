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

package io.suricate.monitoring.repository;

import io.suricate.monitoring.model.entity.Asset;
import io.suricate.monitoring.model.entity.Library;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository used for request Libraries in database
 */
public interface LibraryRepository extends JpaRepository<Library, Long> {

    /**
     * Method used to update library
     *
     * @param technicalName widget technical name
     * @param content       widget content
     * @return State
     */
    @Modifying
    @Query("UPDATE Library " +
        "SET content = :content " +
        "WHERE technicalName = :technicalName")
    int updateLibrary(@Param("technicalName") String technicalName, @Param("content") Asset content);

    /**
     * Method used to get a list of unique library id from widget ids
     *
     * @param widgetIds list of widget ids
     * @return the list of library ids
     */
    @Cacheable("lib-by-widget-id")
    @Query("SELECT DISTINCT l.asset.id " +
        "FROM Widget w " +
        "JOIN w.libraries l " +
        "WHERE w.id in (:ids) ")
    List<Long> getLibs(@Param("ids") List<Long> widgetIds);

    /**
     * Find a librairie by technical name
     *
     * @param technicalname The technical name
     * @return The related library
     */
    Library findByTechnicalName(String technicalname);

}
