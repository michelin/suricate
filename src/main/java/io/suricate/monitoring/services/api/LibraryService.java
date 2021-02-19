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

package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.entities.Library;
import io.suricate.monitoring.model.entities.ProjectWidget;
import io.suricate.monitoring.repositories.LibraryRepository;
import io.suricate.monitoring.utils.IdUtils;
import io.suricate.monitoring.utils.logging.LogExecutionTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manage the libraries
 */
@Service
public class LibraryService {

    /**
     * The library repository
     */
    private final LibraryRepository libraryRepository;

    /**
     * The asset repository
     */
    private final AssetService assetService;

    /**
     * The constructor
     *
     * @param libraryRepository The library repository
     * @param assetService      The asset repository
     */
    @Autowired
    public LibraryService(final LibraryRepository libraryRepository, final AssetService assetService) {
        this.libraryRepository = libraryRepository;
        this.assetService = assetService;
    }

    /**
     * Get all libraries of the given widgets
     *
     * @param widgetInstances The widgets
     * @return The libraries
     */
    @LogExecutionTime
    public List<String> getLibrariesToken(List<ProjectWidget> widgetInstances) {
        List<Long> widgetList = widgetInstances
                .stream()
                .map(projectWidget -> projectWidget.getWidget().getId())
                .distinct()
                .collect(Collectors.toList());

        if (widgetList.isEmpty()) {
            return Collections.emptyList();
        }

        return libraryRepository.getLibs(widgetList)
                .stream()
                .map(IdUtils::encrypt)
                .collect(Collectors.toList());
    }


    /**
     * Update a list of libraries
     *
     * @param libraries All the libraries to add
     * @return All the available libraries
     */
    @Transactional
    public List<Library> updateLibraryInDatabase(List<Library> libraries) {
        if (libraries == null) {
            return Collections.emptyList();
        }

        for (Library library : libraries) {
            Library lib = libraryRepository.findByTechnicalName(library.getTechnicalName());

            if (library.getAsset() != null) {
                if (lib != null && lib.getAsset() != null) {
                    library.getAsset().setId(lib.getAsset().getId());
                }
                assetService.save(library.getAsset());
            }

            if (lib != null) {
                library.setId(lib.getId());
            }
        }

        libraryRepository.saveAll(libraries);

        return libraryRepository.findAll();
    }

}
