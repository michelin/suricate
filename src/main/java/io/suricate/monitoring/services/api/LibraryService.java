/*
 * Copyright 2012-2021 the original author or authors.
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

import io.suricate.monitoring.model.entities.*;
import io.suricate.monitoring.repositories.LibraryRepository;
import io.suricate.monitoring.services.specifications.CategorySearchSpecification;
import io.suricate.monitoring.services.specifications.LibrarySearchSpecification;
import io.suricate.monitoring.utils.IdUtils;
import io.suricate.monitoring.utils.logging.LogExecutionTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
     * Get all the libraries
     *
     * @return The list of libraries
     */
    @Transactional(readOnly = true)
    public Page<Library> getAll(String search, Pageable pageable) {
        return libraryRepository.findAll(new LibrarySearchSpecification(search), pageable);
    }


    /**
     * Get all libraries of a project
     *
     * @param project The project
     * @return The libraries
     */
    @Transactional(readOnly = true)
    public List<Library> getLibrariesByProject(Project project) {
        List<Long> widgetIds = project.getGrids()
                .stream()
                .map(ProjectGrid::getWidgets)
                .flatMap(Collection::stream)
                .map(projectWidget -> projectWidget.getWidget().getId())
                .distinct()
                .collect(Collectors.toList());

        if (widgetIds.isEmpty()) {
            return Collections.emptyList();
        }

        return libraryRepository.findDistinctByWidgetsIdIn(widgetIds);
    }

    /**
     * Get all library tokens of a project
     *
     * @param project The project
     * @return The library tokens
     */
    @Transactional(readOnly = true)
    public List<String> getLibraryTokensByProject(Project project) {
        return getLibrariesByProject(project)
                .stream()
                .map(Library::getId)
                .map(IdUtils::encrypt)
                .collect(Collectors.toList());
    }

    /**
     * Create or update a list of libraries
     * @param libraries All the libraries to create/update
     * @return The created/updated libraries
     */
    @Transactional
    public List<Library> createUpdateLibraries(List<Library> libraries) {
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
