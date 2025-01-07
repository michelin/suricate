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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.michelin.suricate.model.dto.api.export.ImportExportRepositoryDto;
import com.michelin.suricate.model.dto.api.repository.RepositoryRequestDto;
import com.michelin.suricate.model.dto.api.repository.RepositoryResponseDto;
import com.michelin.suricate.model.entity.Repository;
import com.michelin.suricate.model.enumeration.RepositoryTypeEnum;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RepositoryMapperTest {
    @InjectMocks
    private RepositoryMapperImpl repositoryMapper;

    @Test
    void shouldToRepositoryDtoNoWidgets() {
        Repository repository = new Repository();
        repository.setId(1L);
        repository.setName("name");
        repository.setUrl("url");
        repository.setLogin("login");
        repository.setPassword("password");
        repository.setType(RepositoryTypeEnum.LOCAL);
        repository.setLocalPath("localPath");
        repository.setPriority(1);
        repository.setBranch("branch");
        repository.setEnabled(false);
        repository.setCreatedDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));

        RepositoryResponseDto actual = repositoryMapper.toRepositoryDtoNoWidgets(repository);

        assertEquals(1L, actual.getId());
        assertEquals("name", actual.getName());
        assertEquals("url", actual.getUrl());
        assertEquals("login", actual.getLogin());
        assertEquals("password", actual.getPassword());
        assertEquals(RepositoryTypeEnum.LOCAL, actual.getType());
        assertEquals("localPath", actual.getLocalPath());
        assertEquals(1, actual.getPriority());
        assertEquals("branch", actual.getBranch());
        assertFalse(actual.isEnabled());
        assertEquals(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")), actual.getCreatedDate());
    }

    @Test
    void shouldToImportExportRepositoryDto() {
        Repository repository = new Repository();
        repository.setId(1L);
        repository.setName("name");
        repository.setUrl("url");
        repository.setLogin("login");
        repository.setPassword("password");
        repository.setType(RepositoryTypeEnum.LOCAL);
        repository.setLocalPath("localPath");
        repository.setPriority(1);
        repository.setBranch("branch");
        repository.setEnabled(false);
        repository.setCreatedDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));

        ImportExportRepositoryDto actual = repositoryMapper.toImportExportRepositoryDto(repository);

        assertEquals("name", actual.getName());
        assertEquals("url", actual.getUrl());
        assertEquals("login", actual.getLogin());
        assertEquals("password", actual.getPassword());
        assertEquals(RepositoryTypeEnum.LOCAL, actual.getType());
        assertEquals("localPath", actual.getLocalPath());
        assertEquals(1, actual.getPriority());
        assertEquals("branch", actual.getBranch());
        assertFalse(actual.isEnabled());
        assertEquals(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")), actual.getCreatedDate());
    }

    @Test
    void shouldToRepositoryEntity() {
        RepositoryRequestDto repository = new RepositoryRequestDto();
        repository.setName("name");
        repository.setUrl("url");
        repository.setLogin("login");
        repository.setPassword("password");
        repository.setType(RepositoryTypeEnum.LOCAL);
        repository.setLocalPath("localPath");
        repository.setPriority(1);
        repository.setBranch("branch");
        repository.setEnabled(false);

        Repository actual = repositoryMapper.toRepositoryEntity(1L, repository);

        assertEquals("name", actual.getName());
        assertEquals("url", actual.getUrl());
        assertEquals("login", actual.getLogin());
        assertEquals("password", actual.getPassword());
        assertEquals(RepositoryTypeEnum.LOCAL, actual.getType());
        assertEquals("localPath", actual.getLocalPath());
        assertEquals(1, actual.getPriority());
        assertEquals("branch", actual.getBranch());
        assertFalse(actual.isEnabled());
    }

    @Test
    void shouldToRepositoryEntityImportExportRepositoryDto() {
        ImportExportRepositoryDto repository = new ImportExportRepositoryDto();
        repository.setName("name");
        repository.setUrl("url");
        repository.setLogin("login");
        repository.setPassword("password");
        repository.setType(RepositoryTypeEnum.LOCAL);
        repository.setLocalPath("localPath");
        repository.setBranch("branch");
        repository.setEnabled(false);

        Repository actual = repositoryMapper.toRepositoryEntity(repository);

        assertEquals("name", actual.getName());
        assertEquals("url", actual.getUrl());
        assertEquals("login", actual.getLogin());
        assertEquals("password", actual.getPassword());
        assertEquals(RepositoryTypeEnum.LOCAL, actual.getType());
        assertEquals("localPath", actual.getLocalPath());
        assertEquals("branch", actual.getBranch());
        assertFalse(actual.isEnabled());
    }
}
