package com.michelin.suricate.services.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entities.Asset;
import com.michelin.suricate.model.entities.Library;
import com.michelin.suricate.model.entities.Library_;
import com.michelin.suricate.model.entities.Project;
import com.michelin.suricate.model.entities.ProjectGrid;
import com.michelin.suricate.model.entities.ProjectWidget;
import com.michelin.suricate.model.entities.Widget;
import com.michelin.suricate.repositories.LibraryRepository;
import com.michelin.suricate.services.specifications.LibrarySearchSpecification;
import com.michelin.suricate.utils.IdUtils;
import java.util.Collections;
import java.util.List;
import javax.persistence.metamodel.SingularAttribute;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class LibraryServiceTest {
    @Mock
    private LibraryRepository libraryRepository;

    @Mock
    private AssetService assetService;

    @Mock
    private SingularAttribute<Library, String> technicalName;

    @InjectMocks
    private LibraryService libraryService;

    @Test
    void shouldGetAll() {
        Library library = new Library();
        library.setId(1L);

        Library_.technicalName = technicalName;
        when(libraryRepository.findAll(any(LibrarySearchSpecification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(Collections.singletonList(library)));

        Page<Library> actual = libraryService.getAll("search", Pageable.unpaged());

        assertThat(actual)
            .isNotEmpty()
            .contains(library);

        verify(libraryRepository)
            .findAll(Mockito.<LibrarySearchSpecification>argThat(
                    specification -> specification.getSearch().equals("search")
                        && specification.getAttributes().contains(technicalName.getName())),
                Mockito.<Pageable>argThat(pageable -> pageable.equals(Pageable.unpaged())));
    }

    @Test
    void shouldGetLibrariesByProjectWhenNoWidget() {
        Project project = new Project();
        project.setGrids(Collections.singleton(new ProjectGrid()));

        List<Library> actual = libraryService.getLibrariesByProject(project);

        assertThat(actual).isEmpty();

        verify(libraryRepository, times(0))
            .findDistinctByWidgetsIdIn(any());
    }

    @Test
    void shouldGetLibrariesByProject() {
        Widget widget = new Widget();
        widget.setId(1L);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setWidget(widget);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setWidgets(Collections.singleton(projectWidget));

        Project project = new Project();
        project.setGrids(Collections.singleton(projectGrid));

        Library library = new Library();

        when(libraryRepository.findDistinctByWidgetsIdIn(any()))
            .thenReturn(Collections.singletonList(library));

        List<Library> actual = libraryService.getLibrariesByProject(project);

        assertThat(actual)
            .isNotEmpty()
            .contains(library);

        verify(libraryRepository)
            .findDistinctByWidgetsIdIn(Collections.singletonList(1L));
    }

    @Test
    void shouldGetLibraryTokensByProject() {
        try (MockedStatic<IdUtils> mocked = mockStatic(IdUtils.class)) {
            Widget widget = new Widget();
            widget.setId(1L);

            ProjectWidget projectWidget = new ProjectWidget();
            projectWidget.setWidget(widget);

            ProjectGrid projectGrid = new ProjectGrid();
            projectGrid.setWidgets(Collections.singleton(projectWidget));

            Project project = new Project();
            project.setGrids(Collections.singleton(projectGrid));

            Asset asset = new Asset();
            asset.setId(1L);

            Library library = new Library();
            library.setAsset(asset);

            mocked.when(() -> IdUtils.encrypt(1L))
                .thenReturn("token");
            when(libraryRepository.findDistinctByWidgetsIdIn(any()))
                .thenReturn(Collections.singletonList(library));

            List<String> actual = libraryService.getLibraryTokensByProject(project);
            assertThat(actual)
                .isNotEmpty()
                .contains("token");

            verify(libraryRepository)
                .findDistinctByWidgetsIdIn(Collections.singletonList(1L));
        }
    }

    @Test
    void shouldCreateUpdateLibrariesWhenNull() {
        List<Library> actual = libraryService.createUpdateLibraries(null);

        assertThat(actual).isEmpty();

        verify(libraryRepository, times(0))
            .findByTechnicalName(any());
        verify(assetService, times(0))
            .save(any());
        verify(libraryRepository, times(0))
            .saveAll(any());
        verify(libraryRepository, times(0))
            .findAll();
    }

    @Test
    void shouldCreateLibraries() {
        Asset asset = new Asset();
        asset.setId(1L);

        Library library = new Library();
        library.setAsset(asset);
        library.setTechnicalName("technicalName");

        when(libraryRepository.findByTechnicalName(any()))
            .thenReturn(null);
        when(assetService.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));
        when(libraryRepository.saveAll(any()))
            .thenAnswer(answer -> answer.getArgument(0));
        when(libraryRepository.findAll())
            .thenReturn(Collections.singletonList(library));

        List<Library> actual = libraryService.createUpdateLibraries(Collections.singletonList(library));

        assertThat(actual)
            .isNotEmpty()
            .contains(library);

        verify(libraryRepository)
            .findByTechnicalName("technicalName");
        verify(assetService)
            .save(asset);
        verify(libraryRepository)
            .saveAll(Collections.singletonList(library));
        verify(libraryRepository)
            .findAll();
    }

    @Test
    void shouldCreateLibrariesNoAsset() {
        Library library = new Library();
        library.setTechnicalName("technicalName");

        when(libraryRepository.findByTechnicalName(any()))
            .thenReturn(null);
        when(libraryRepository.saveAll(any()))
            .thenAnswer(answer -> answer.getArgument(0));
        when(libraryRepository.findAll())
            .thenReturn(Collections.singletonList(library));

        List<Library> actual = libraryService.createUpdateLibraries(Collections.singletonList(library));

        assertThat(actual)
            .isNotEmpty()
            .contains(library);

        verify(libraryRepository)
            .findByTechnicalName("technicalName");
        verify(assetService, times(0))
            .save(any());
        verify(libraryRepository)
            .saveAll(Collections.singletonList(library));
        verify(libraryRepository)
            .findAll();
    }

    @Test
    void shouldUpdateLibraries() {
        Asset oldAsset = new Asset();
        oldAsset.setId(2L);

        Library oldLibrary = new Library();
        oldLibrary.setId(2L);
        oldLibrary.setAsset(oldAsset);

        Library library = new Library();
        library.setAsset(new Asset());
        library.setTechnicalName("technicalName");

        when(libraryRepository.findByTechnicalName(any()))
            .thenReturn(oldLibrary);
        when(assetService.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));
        when(libraryRepository.saveAll(any()))
            .thenAnswer(answer -> answer.getArgument(0));
        when(libraryRepository.findAll())
            .thenReturn(Collections.singletonList(library));

        List<Library> actual = libraryService.createUpdateLibraries(Collections.singletonList(library));

        assertThat(actual).isNotEmpty();
        assertThat(actual.get(0)).isEqualTo(library);
        assertThat(actual.get(0).getId()).isEqualTo(2L);
        assertThat(actual.get(0).getAsset().getId()).isEqualTo(2L);

        verify(libraryRepository)
            .findByTechnicalName("technicalName");
        verify(assetService)
            .save(argThat(createdAsset -> createdAsset.getId().equals(2L)));
        verify(libraryRepository)
            .saveAll(Collections.singletonList(library));
        verify(libraryRepository)
            .findAll();
    }

    @Test
    void shouldUpdateLibrariesNoAssetBefore() {
        Library oldLibrary = new Library();
        oldLibrary.setId(2L);

        Library library = new Library();
        library.setAsset(new Asset());
        library.setTechnicalName("technicalName");

        when(libraryRepository.findByTechnicalName(any()))
            .thenReturn(oldLibrary);
        when(assetService.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));
        when(libraryRepository.saveAll(any()))
            .thenAnswer(answer -> answer.getArgument(0));
        when(libraryRepository.findAll())
            .thenReturn(Collections.singletonList(library));

        List<Library> actual = libraryService.createUpdateLibraries(Collections.singletonList(library));

        assertThat(actual).isNotEmpty();
        assertThat(actual.get(0)).isEqualTo(library);
        assertThat(actual.get(0).getId()).isEqualTo(2L);
        assertThat(actual.get(0).getAsset()).isNotNull();

        verify(libraryRepository)
            .findByTechnicalName("technicalName");
        verify(assetService)
            .save(argThat(createdAsset -> createdAsset.getId() == null));
        verify(libraryRepository)
            .saveAll(Collections.singletonList(library));
        verify(libraryRepository)
            .findAll();
    }
}
