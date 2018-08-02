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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Method used to initialize lucene index
 */
@Component
public class SearchIndexBuilder implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * The search service
     */
    private final SearchService searchService;

    @Autowired
    public SearchIndexBuilder(final SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Re-index on startup.
     * This method is called on Spring's startup.
     */
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        searchService.runSearchIndexer();
    }


}

