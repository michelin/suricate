/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.michelin.suricate.service.mapper;

import com.michelin.suricate.model.dto.api.export.ImportExportRepositoryDto;
import com.michelin.suricate.model.dto.api.repository.RepositoryRequestDto;
import com.michelin.suricate.model.dto.api.repository.RepositoryResponseDto;
import com.michelin.suricate.model.entity.Repository;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * Repository mapper.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class RepositoryMapper {
    /**
     * Map a repository into a DTO.
     * Ignore the widgets repository
     *
     * @param repository The repository to map
     * @return The repository as DTO
     */
    @Named("toRepositoryDtoNoWidgets")
    public abstract RepositoryResponseDto toRepositoryDtoNoWidgets(Repository repository);

    /**
     * Map a repository DTO as entity.
     *
     * @param id                   The repository id
     * @param repositoryRequestDto The repository DTO to map
     * @return The repository as entity
     */
    @Named("toRepositoryEntity")
    public abstract Repository toRepositoryEntity(Long id, RepositoryRequestDto repositoryRequestDto);

    /**
     * Map an import export repository DTO as entity.
     *
     * @param importExportRepositoryDto The repository DTO to map
     * @return The repository as entity
     */
    @Named("toRepositoryEntity")
    public abstract Repository toRepositoryEntity(ImportExportRepositoryDto importExportRepositoryDto);

    /**
     * Map a repository into an import export repository DTO.
     *
     * @param repository The repository to map
     * @return The import export repository as DTO
     */
    @Named("toImportExportRepositoryDto")
    public abstract ImportExportRepositoryDto toImportExportRepositoryDto(Repository repository);
}
