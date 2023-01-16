package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.entities.Repository;
import io.suricate.monitoring.model.entities.Repository_;
import io.suricate.monitoring.repositories.RepositoryRepository;
import io.suricate.monitoring.services.specifications.RepositorySearchSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.metamodel.SingularAttribute;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        assertThat(actual)
                .isNotEmpty()
                .contains(repository);

        verify(repositoryRepository, times(1))
                .findAll(Mockito.<RepositorySearchSpecification>argThat(specification -> specification.getSearch().equals("search") &&
                                specification.getAttributes().contains(name.getName())),
                        Mockito.<Pageable>argThat(pageable -> pageable.equals(Pageable.unpaged())));
    }

    @Test
    void shouldFindAllByEnabledOrderByPriorityDescCreatedDateAsc() {
        Repository repository = new Repository();
        repository.setId(1L);

        when(repositoryRepository.findAllByEnabledOrderByPriorityDescCreatedDateAsc(true))
                .thenReturn(Optional.of(Collections.singletonList(repository)));

        Optional<List<Repository>> actual = repositoryService.findAllByEnabledOrderByPriorityDescCreatedDateAsc(true);

        assertThat(actual).isPresent();
        assertThat(actual.get())
                .isNotEmpty()
                .contains(repository);

        verify(repositoryRepository, times(1))
                .findAllByEnabledOrderByPriorityDescCreatedDateAsc(true);
    }

    @Test
    void shouldGetOneById() {
        Repository repository = new Repository();
        repository.setId(1L);

        when(repositoryRepository.findById(any()))
                .thenReturn(Optional.of(repository));

        Optional<Repository> actual = repositoryService.getOneById(1L);

        assertThat(actual)
                .isPresent()
                .contains(repository);

        verify(repositoryRepository, times(1))
                .findById(1L);
    }

    @Test
    void shouldFindByName() {
        Repository repository = new Repository();
        repository.setId(1L);

        when(repositoryRepository.findByName(any()))
                .thenReturn(Optional.of(repository));

        Optional<Repository> actual = repositoryService.findByName("name");

        assertThat(actual)
                .isPresent()
                .contains(repository);

        verify(repositoryRepository, times(1))
                .findByName("name");
    }

    @Test
    void shouldExistsById() {
        when(repositoryRepository.existsById(any()))
                .thenReturn(true);

        boolean actual = repositoryService.existsById(1L);

        assertThat(actual)
                .isTrue();

        verify(repositoryRepository, times(1))
                .existsById(1L);
    }

    @Test
    void shouldAddOrUpdateRepository() {
        Repository repository = new Repository();
        repository.setId(1L);

        when(repositoryRepository.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));

        repositoryService.addOrUpdateRepository(repository);

        verify(repositoryRepository, times(1))
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

        verify(repositoryRepository, times(1))
                .saveAll(repositories);
    }
}
