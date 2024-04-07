package com.michelin.suricate.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

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

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getName()).isEqualTo("name");
        assertThat(actual.getUrl()).isEqualTo("url");
        assertThat(actual.getLogin()).isEqualTo("login");
        assertThat(actual.getPassword()).isEqualTo("password");
        assertThat(actual.getType()).isEqualTo(RepositoryTypeEnum.LOCAL);
        assertThat(actual.getLocalPath()).isEqualTo("localPath");
        assertThat(actual.getPriority()).isEqualTo(1);
        assertThat(actual.getBranch()).isEqualTo("branch");
        assertThat(actual.isEnabled()).isFalse();
        assertThat(actual.getCreatedDate()).isEqualTo(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
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

        assertThat(actual.getName()).isEqualTo("name");
        assertThat(actual.getUrl()).isEqualTo("url");
        assertThat(actual.getLogin()).isEqualTo("login");
        assertThat(actual.getPassword()).isEqualTo("password");
        assertThat(actual.getType()).isEqualTo(RepositoryTypeEnum.LOCAL);
        assertThat(actual.getLocalPath()).isEqualTo("localPath");
        assertThat(actual.getBranch()).isEqualTo("branch");
        assertThat(actual.isEnabled()).isFalse();
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

        assertThat(actual.getName()).isEqualTo("name");
        assertThat(actual.getUrl()).isEqualTo("url");
        assertThat(actual.getLogin()).isEqualTo("login");
        assertThat(actual.getPassword()).isEqualTo("password");
        assertThat(actual.getType()).isEqualTo(RepositoryTypeEnum.LOCAL);
        assertThat(actual.getLocalPath()).isEqualTo("localPath");
        assertThat(actual.getPriority()).isEqualTo(1);
        assertThat(actual.getBranch()).isEqualTo("branch");
        assertThat(actual.isEnabled()).isFalse();
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

        assertThat(actual.getName()).isEqualTo("name");
        assertThat(actual.getUrl()).isEqualTo("url");
        assertThat(actual.getLogin()).isEqualTo("login");
        assertThat(actual.getPassword()).isEqualTo("password");
        assertThat(actual.getType()).isEqualTo(RepositoryTypeEnum.LOCAL);
        assertThat(actual.getLocalPath()).isEqualTo("localPath");
        assertThat(actual.getBranch()).isEqualTo("branch");
        assertThat(actual.isEnabled()).isFalse();
    }
}
