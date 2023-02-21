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

package com.michelin.suricate.services.mapper;

import com.michelin.suricate.model.dto.api.export.ImportExportRepositoryDto;
import com.michelin.suricate.model.dto.api.repository.RepositoryRequestDto;
import com.michelin.suricate.model.dto.api.repository.RepositoryResponseDto;
import com.michelin.suricate.model.entities.Repository;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

/**
 * Manage the generation DTO/Model objects for Widget class
 */
@Mapper(componentModel = "spring")
public abstract class RepositoryMapper {
    /**
     * Map a repository into a DTO.
     * Ignore the widgets repository
     *
     * @param repository The repository to map
     * @return The repository as DTO
     */
    @Named("toRepositoryDTONoWidgets")
    public abstract RepositoryResponseDto toRepositoryDTONoWidgets(Repository repository);

    /**
     * Map a repository DTO as entity
     *
     * @param repositoryId         The repository id
     * @param repositoryRequestDto The repository DTO to map
     * @return The repository as entity
     */
    @Named("toRepositoryEntity")
    public abstract Repository toRepositoryEntity(Long id, RepositoryRequestDto repositoryRequestDto);

    /**
     * Map a repository into an import export repository DTO
     *
     * @param repository The repository to map
     * @return The import export repository as DTO
     */
    @Named("toImportExportRepositoryDTO")
    public abstract ImportExportRepositoryDto toImportExportRepositoryDTO(Repository repository);

    /**
     * Map an import export repository DTO as entity
     * @param repositoryRequestDto The repository DTO to map
     * @return The repository as entity
     */
    @Named("toRepositoryEntity")
    public abstract Repository toRepositoryEntity(ImportExportRepositoryDto importExportRepositoryDto);
}
