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

package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.repository.RepositoryRequestDto;
import io.suricate.monitoring.model.dto.api.repository.RepositoryResponseDto;
import io.suricate.monitoring.model.entities.Repository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Manage the generation DTO/Model objects for Widget class
 */
@Mapper(componentModel = "spring")
public interface RepositoryMapper {

    /**
     * Map a repository into a DTO
     *
     * @param repository The repository to map
     * @return The repository as DTO
     */
    @Named("toRepositoryDTO")
    RepositoryResponseDto toRepositoryDTO(Repository repository);

    /**
     * Map a repository DTO as entity
     *
     * @param repositoryId         The repository id
     * @param repositoryRequestDto The repository DTO to map
     * @return The repository as entity
     */
    @Named("toRepositoryEntity")
    @Mapping(target = "widgets", ignore = true)
    Repository toRepositoryEntity(Long id, RepositoryRequestDto repositoryRequestDto);
}
