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

package io.suricate.monitoring.service;

import io.suricate.monitoring.model.entity.Library;
import io.suricate.monitoring.model.dto.widget.WidgetResponse;
import io.suricate.monitoring.repository.AssetRepository;
import io.suricate.monitoring.repository.LibraryRepository;
import io.suricate.monitoring.utils.IdUtils;
import io.suricate.monitoring.utils.logging.LogExecutionTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    private final LibraryRepository libraryRepository;

    private final AssetRepository assetRepository;

    @Autowired
    public LibraryService(LibraryRepository libraryRepository, AssetRepository assetRepository) {
        this.libraryRepository = libraryRepository;
        this.assetRepository = assetRepository;
    }

    /**
     * Method used to get all library for the displayed widget
     * @param response
     * @return
     */
    @LogExecutionTime
    public List<String> getLibraries(List<WidgetResponse> response) {
        List<Long> widgetList = response.stream().map( WidgetResponse::getWidgetId).distinct().collect(Collectors.toList());
        if (widgetList.isEmpty()){
            return null;
        }
        List<Long> ids = libraryRepository.getLibs(widgetList);
        return ids.stream().map(IdUtils::encrypt).collect(Collectors.toList());
    }


    /**
     * Method used to update library in database
     * @param list all library to add
     * @return the list of library available in database
     */
    @Transactional
    public List<Library> updateLibraryInDatabase(List<Library> list) {
        if (list == null) {
            return null;
        }
        for (Library library : list) {
            Library lib = libraryRepository.findByTechnicalName(library.getTechnicalName());
            if (library.getAsset() != null){
                if (lib != null && lib.getAsset() != null) {
                    library.getAsset().setId(lib.getAsset().getId());
                }
                assetRepository.save(library.getAsset());
            }
            if (lib != null) {
                library.setId(lib.getId());
            }
        }
        libraryRepository.save(list);
        return libraryRepository.findAll();
    }

}
