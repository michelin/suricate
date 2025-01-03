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
