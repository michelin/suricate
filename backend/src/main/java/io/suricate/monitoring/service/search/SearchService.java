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

package io.suricate.monitoring.service.search;

import io.suricate.monitoring.model.entity.Widget;
import io.suricate.monitoring.model.enums.WidgetAvailabilityEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Search service used to request indexed data
 */
@Service
public class SearchService {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method used to search widget using lucene
     * @param searchQuery the search query terms
     * @param widgetAvailability widget availability
     * @return the list of widgets
     */
    public List<Widget> searchWidgets(WidgetAvailabilityEnum widgetAvailability, String searchQuery) {

        // Query questions first
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

        QueryBuilder widgetQb =  fullTextEntityManager.getSearchFactory().buildQueryBuilder()
                .forEntity(Widget.class)
                .get();

        Query questionQuery = widgetQb.bool()
                // Check availability
                .must(widgetQb.keyword().onField("widgetAvailability").matching(widgetAvailability).createQuery())
                // Check category and widget name
                .must(widgetQb.keyword().wildcard().onField("name").andField("description").andField("category.name").matching("*"+ StringUtils.lowerCase(searchQuery)+"*").createQuery())
                .createQuery();

        FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(questionQuery, Widget.class);
        // Sort response
        Sort sort = new Sort( new SortField( "name", SortField.Type.STRING, false ) );
        jpaQuery.setSort( sort );

        return (List<Widget>) jpaQuery.getResultList();
    }

    /**
     * Create an initial Lucene index for the data already present in the database.
     * Run on a separate thread.
     */
    @Async
    public void runSearchIndexer() {
        try {
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }
}
