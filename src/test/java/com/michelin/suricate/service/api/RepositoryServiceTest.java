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

package com.michelin.suricate.service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entity.Repository;
import com.michelin.suricate.model.entity.Repository_;
import com.michelin.suricate.repository.RepositoryRepository;
import com.michelin.suricate.service.specification.RepositorySearchSpecification;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class RepositoryServiceTest {
    @Mock
    private SingularAttribute<Repository, String> name;

    @Mock
    private RepositoryRepository repositoryRepository;

    @InjectMocks
    private RepositoryService repositoryService;

    @Test
    void shouldGetAll() {
        Repository repository = new Repository();
        repository.setId(1L);

        Repository_.name = name;
        when(repositoryRepository.findAll(any(RepositorySearchSpecification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(repository)));

        Page<Repository> actual = repositoryService.getAll("search", Pageable.unpaged());

        assertFalse(actual.isEmpty());
        assertEquals(repository, actual.get().toList().getFirst());

        verify(repositoryRepository)
            .findAll(Mockito.<RepositorySearchSpecification>argThat(
                    specification -> specification.getSearch().equals("search")
                        && specification.getAttributes().contains(name.getName())),
                Mockito.<Pageable>argThat(pageable -> pageable.equals(Pageable.unpaged())));
    }

    @Test
    void shouldFindAllByEnabledOrderByPriorityDescCreatedDateAsc() {
        Repository repository = new Repository();
        repository.setId(1L);

        when(repositoryRepository.findAllByEnabledOrderByPriorityDescCreatedDateAsc(true))
            .thenReturn(Optional.of(Collections.singletonList(repository)));

        Optional<List<Repository>> actual = repositoryService.findAllByEnabledOrderByPriorityDescCreatedDateAsc(true);

        assertTrue(actual.isPresent());
        assertFalse(actual.get().isEmpty());
        assertEquals(repository, actual.get().getFirst());

        verify(repositoryRepository)
            .findAllByEnabledOrderByPriorityDescCreatedDateAsc(true);
    }

    @Test
    void shouldGetOneById() {
        Repository repository = new Repository();
        repository.setId(1L);

        when(repositoryRepository.findById(any()))
            .thenReturn(Optional.of(repository));

        Optional<Repository> actual = repositoryService.getOneById(1L);

        assertTrue(actual.isPresent());
        assertEquals(repository, actual.get());

        verify(repositoryRepository)
            .findById(1L);
    }

    @Test
    void shouldFindByName() {
        Repository repository = new Repository();
        repository.setId(1L);

        when(repositoryRepository.findByName(any()))
            .thenReturn(Optional.of(repository));

        Optional<Repository> actual = repositoryService.findByName("name");

        assertTrue(actual.isPresent());
        assertEquals(repository, actual.get());

        verify(repositoryRepository)
            .findByName("name");
    }

    @Test
    void shouldExistsById() {
        when(repositoryRepository.existsById(any()))
            .thenReturn(true);

        boolean actual = repositoryService.existsById(1L);

        assertTrue(actual);

        verify(repositoryRepository)
            .existsById(1L);
    }

    @Test
    void shouldAddOrUpdateRepository() {
        Repository repository = new Repository();
        repository.setId(1L);

        when(repositoryRepository.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));

        repositoryService.addOrUpdateRepository(repository);

        verify(repositoryRepository)
            .save(repository);
    }

    @Test
    void shouldAddOrUpdateRepositories() {
        Repository repository = new Repository();
        repository.setId(1L);
        List<Repository> repositories = Collections.singletonList(repository);

        when(repositoryRepository.saveAll(any()))
            .thenAnswer(answer -> answer.getArgument(0));

        repositoryService.addOrUpdateRepositories(Collections.singletonList(repository));

        verify(repositoryRepository)
            .saveAll(repositories);
    }
}
