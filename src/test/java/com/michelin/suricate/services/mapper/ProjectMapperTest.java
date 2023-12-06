package com.michelin.suricate.services.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.asset.AssetResponseDto;
import com.michelin.suricate.model.dto.api.export.ImportExportAssetDto;
import com.michelin.suricate.model.dto.api.export.ImportExportProjectDto;
import com.michelin.suricate.model.dto.api.project.GridPropertiesResponseDto;
import com.michelin.suricate.model.dto.api.project.ProjectRequestDto;
import com.michelin.suricate.model.dto.api.project.ProjectResponseDto;
import com.michelin.suricate.model.dto.api.projectgrid.ProjectGridResponseDto;
import com.michelin.suricate.model.entities.Asset;
import com.michelin.suricate.model.entities.Project;
import com.michelin.suricate.model.entities.ProjectGrid;
import com.michelin.suricate.model.entities.ProjectWidget;
import com.michelin.suricate.services.api.LibraryService;
import com.michelin.suricate.utils.IdUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectMapperTest {
    @Mock
    private AssetMapper assetMapper;

    @Mock
    private ProjectGridMapper projectGridMapper;

    @Mock
    private LibraryService libraryService;

    @InjectMocks
    private ProjectMapperImpl projectMapper;

    @NotNull
    private static ImportExportProjectDto getImportExportProjectDto(ImportExportAssetDto importExportAssetDto,
                                                                    GridPropertiesResponseDto gridPropsResponseDto) {
        ImportExportProjectDto importExportProjectDto = new ImportExportProjectDto();
        importExportProjectDto.setName("name");
        importExportProjectDto.setToken("token");
        importExportProjectDto.setDisplayProgressBar(true);
        importExportProjectDto.setImage(importExportAssetDto);
        importExportProjectDto.setGridProperties(gridPropsResponseDto);

        ImportExportProjectDto.ImportExportProjectGridDto
            importExportProjectGridDto = getImportExportProjectGridDto();

        importExportProjectDto.setGrids(Collections.singletonList(importExportProjectGridDto));
        return importExportProjectDto;
    }

    @NotNull
    private static ImportExportProjectDto.ImportExportProjectGridDto getImportExportProjectGridDto() {
        ImportExportProjectDto.ImportExportProjectGridDto.ImportExportProjectWidgetDto importExportProjectWidgetDto =
            new ImportExportProjectDto.ImportExportProjectGridDto.ImportExportProjectWidgetDto();
        importExportProjectWidgetDto.setId(1L);

        ImportExportProjectDto.ImportExportProjectGridDto importExportProjectGridDto =
            new ImportExportProjectDto.ImportExportProjectGridDto();
        importExportProjectGridDto.setId(1L);
        importExportProjectGridDto.setTime(10);
        importExportProjectGridDto.setWidgets(Collections.singletonList(importExportProjectWidgetDto));
        return importExportProjectGridDto;
    }

    @Test
    void shouldToProjectDto() {
        try (MockedStatic<IdUtils> mocked = mockStatic(IdUtils.class)) {
            Asset asset = new Asset();
            asset.setId(1L);
            asset.setSize(10);
            asset.setContentType("contentType");
            asset.setContent(new byte[10]);

            ProjectGrid projectGrid = new ProjectGrid();
            projectGrid.setId(1L);
            projectGrid.setTime(10);

            Project project = new Project();
            project.setToken("token");
            project.setName("name");
            project.setMaxColumn(1);
            project.setWidgetHeight(1);
            project.setCssStyle("style");
            project.setScreenshot(asset);
            project.setGrids(Collections.singleton(projectGrid));

            AssetResponseDto image = new AssetResponseDto();
            image.setId(1L);
            image.setSize(10);
            image.setContentType("contentType");
            image.setContent(new byte[10]);

            ProjectGridResponseDto grid = new ProjectGridResponseDto();
            grid.setId(1L);
            grid.setTime(10);

            mocked.when(() -> IdUtils.encrypt(1L))
                .thenReturn("encrypted");
            when(libraryService.getLibraryTokensByProject(any()))
                .thenReturn(Collections.singletonList("libraryToken"));
            when(assetMapper.toAssetDto(any()))
                .thenReturn(image);
            when(projectGridMapper.toProjectGridDto(any()))
                .thenReturn(grid);

            ProjectResponseDto actual = projectMapper.toProjectDto(project);

            assertThat(actual.getToken()).isEqualTo("token");
            assertThat(actual.getName()).isEqualTo("name");
            assertThat(actual.getScreenshotToken()).isEqualTo("encrypted");
            assertThat(actual.getLibrariesToken().get(0)).isEqualTo("libraryToken");
            assertThat(actual.getGridProperties().getMaxColumn()).isEqualTo(1);
            assertThat(actual.getGridProperties().getWidgetHeight()).isEqualTo(1);
            assertThat(actual.getGridProperties().getCssStyle()).isEqualTo("style");
            Assertions.assertThat(actual.getImage().getId()).isEqualTo(1L);
            Assertions.assertThat(actual.getImage().getContent()).isEqualTo(new byte[10]);
            Assertions.assertThat(actual.getImage().getSize()).isEqualTo(10);
            Assertions.assertThat(actual.getImage().getContentType()).isEqualTo("contentType");
            Assertions.assertThat(actual.getGrids().get(0).getId()).isEqualTo(1L);
            Assertions.assertThat(actual.getGrids().get(0).getTime()).isEqualTo(10);
        }
    }

    @Test
    void shouldToProjectDtoNoLibrary() {
        try (MockedStatic<IdUtils> mocked = mockStatic(IdUtils.class)) {
            Asset asset = new Asset();
            asset.setId(1L);
            asset.setSize(10);
            asset.setContentType("contentType");
            asset.setContent(new byte[10]);

            ProjectGrid projectGrid = new ProjectGrid();
            projectGrid.setId(1L);
            projectGrid.setTime(10);

            Project project = new Project();
            project.setToken("token");
            project.setName("name");
            project.setMaxColumn(1);
            project.setWidgetHeight(1);
            project.setCssStyle("style");
            project.setScreenshot(asset);
            project.setGrids(Collections.singleton(projectGrid));

            AssetResponseDto image = new AssetResponseDto();
            image.setId(1L);
            image.setSize(10);
            image.setContentType("contentType");
            image.setContent(new byte[10]);

            ProjectGridResponseDto grid = new ProjectGridResponseDto();
            grid.setId(1L);
            grid.setTime(10);

            mocked.when(() -> IdUtils.encrypt(1L))
                .thenReturn("encrypted");
            when(assetMapper.toAssetDto(any()))
                .thenReturn(image);
            when(projectGridMapper.toProjectGridDto(any()))
                .thenReturn(grid);

            ProjectResponseDto actual = projectMapper.toProjectDtoNoLibrary(project);

            assertThat(actual.getToken()).isEqualTo("token");
            assertThat(actual.getName()).isEqualTo("name");
            assertThat(actual.getLibrariesToken()).isEmpty();
            assertThat(actual.getScreenshotToken()).isEqualTo("encrypted");
            assertThat(actual.getGridProperties().getMaxColumn()).isEqualTo(1);
            assertThat(actual.getGridProperties().getWidgetHeight()).isEqualTo(1);
            assertThat(actual.getGridProperties().getCssStyle()).isEqualTo("style");
            Assertions.assertThat(actual.getImage().getId()).isEqualTo(1L);
            Assertions.assertThat(actual.getImage().getContent()).isEqualTo(new byte[10]);
            Assertions.assertThat(actual.getImage().getSize()).isEqualTo(10);
            Assertions.assertThat(actual.getImage().getContentType()).isEqualTo("contentType");
            Assertions.assertThat(actual.getGrids().get(0).getId()).isEqualTo(1L);
            Assertions.assertThat(actual.getGrids().get(0).getTime()).isEqualTo(10);
        }
    }

    @Test
    void shouldToProjectDtoNoAsset() {
        Asset asset = new Asset();
        asset.setId(1L);
        asset.setSize(10);
        asset.setContentType("contentType");
        asset.setContent(new byte[10]);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setTime(10);

        Project project = new Project();
        project.setToken("token");
        project.setName("name");
        project.setMaxColumn(1);
        project.setWidgetHeight(1);
        project.setCssStyle("style");
        project.setScreenshot(asset);
        project.setGrids(Collections.singleton(projectGrid));

        ProjectGridResponseDto grid = new ProjectGridResponseDto();
        grid.setId(1L);
        grid.setTime(10);

        when(projectGridMapper.toProjectGridDto(any()))
            .thenReturn(grid);

        ProjectResponseDto actual = projectMapper.toProjectDtoNoAsset(project);

        assertThat(actual.getToken()).isEqualTo("token");
        assertThat(actual.getName()).isEqualTo("name");
        assertThat(actual.getLibrariesToken()).isEmpty();
        assertThat(actual.getScreenshotToken()).isNull();
        assertThat(actual.getGridProperties().getMaxColumn()).isEqualTo(1);
        assertThat(actual.getGridProperties().getWidgetHeight()).isEqualTo(1);
        assertThat(actual.getGridProperties().getCssStyle()).isEqualTo("style");
        Assertions.assertThat(actual.getImage()).isNull();
    }

    @Test
    void shouldToProjectsDtos() {
        try (MockedStatic<IdUtils> mocked = mockStatic(IdUtils.class)) {
            Asset asset = new Asset();
            asset.setId(1L);
            asset.setSize(10);
            asset.setContentType("contentType");
            asset.setContent(new byte[10]);

            ProjectGrid projectGrid = new ProjectGrid();
            projectGrid.setId(1L);
            projectGrid.setTime(10);

            Project project = new Project();
            project.setToken("token");
            project.setName("name");
            project.setMaxColumn(1);
            project.setWidgetHeight(1);
            project.setCssStyle("style");
            project.setScreenshot(asset);
            project.setGrids(Collections.singleton(projectGrid));

            AssetResponseDto image = new AssetResponseDto();
            image.setId(1L);
            image.setSize(10);
            image.setContentType("contentType");
            image.setContent(new byte[10]);

            ProjectGridResponseDto grid = new ProjectGridResponseDto();
            grid.setId(1L);
            grid.setTime(10);

            mocked.when(() -> IdUtils.encrypt(1L))
                .thenReturn("encrypted");
            when(assetMapper.toAssetDto(any()))
                .thenReturn(image);
            when(projectGridMapper.toProjectGridDto(any()))
                .thenReturn(grid);

            List<ProjectResponseDto> actual = projectMapper.toProjectsDtos(Collections.singletonList(project));

            assertThat(actual.get(0).getToken()).isEqualTo("token");
            assertThat(actual.get(0).getName()).isEqualTo("name");
            assertThat(actual.get(0).getLibrariesToken()).isEmpty();
            assertThat(actual.get(0).getScreenshotToken()).isEqualTo("encrypted");
            assertThat(actual.get(0).getGridProperties().getMaxColumn()).isEqualTo(1);
            assertThat(actual.get(0).getGridProperties().getWidgetHeight()).isEqualTo(1);
            assertThat(actual.get(0).getGridProperties().getCssStyle()).isEqualTo("style");
            Assertions.assertThat(actual.get(0).getImage().getId()).isEqualTo(1L);
            Assertions.assertThat(actual.get(0).getImage().getContent()).isEqualTo(new byte[10]);
            Assertions.assertThat(actual.get(0).getImage().getSize()).isEqualTo(10);
            Assertions.assertThat(actual.get(0).getImage().getContentType()).isEqualTo("contentType");
            Assertions.assertThat(actual.get(0).getGrids().get(0).getId()).isEqualTo(1L);
            Assertions.assertThat(actual.get(0).getGrids().get(0).getTime()).isEqualTo(10);
        }
    }

    @Test
    void shouldToProjectEntity() {
        ProjectRequestDto projectRequestDto = new ProjectRequestDto();
        projectRequestDto.setName("name");
        projectRequestDto.setCssStyle("style");
        projectRequestDto.setMaxColumn(1);
        projectRequestDto.setWidgetHeight(1);

        Project actual = projectMapper.toProjectEntity(projectRequestDto);

        assertThat(actual.getName()).isEqualTo("name");
        assertThat(actual.getCssStyle()).isEqualTo("style");
        assertThat(actual.getMaxColumn()).isEqualTo(1);
        assertThat(actual.getWidgetHeight()).isEqualTo(1);
    }

    @Test
    void shouldToImportExportProjectDto() {
        Asset asset = new Asset();
        asset.setSize(10);
        asset.setContentType("contentType");
        asset.setContent(new byte[10]);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setTime(10);

        Project project = new Project();
        project.setToken("token");
        project.setName("name");
        project.setCssStyle("style");
        project.setMaxColumn(1);
        project.setWidgetHeight(1);
        project.setDisplayProgressBar(true);
        project.setScreenshot(asset);
        project.setGrids(Collections.singleton(projectGrid));

        ImportExportAssetDto importExportAssetDto = new ImportExportAssetDto();
        importExportAssetDto.setSize(10);
        importExportAssetDto.setContentType("contentType");
        importExportAssetDto.setContent(new byte[10]);

        when(assetMapper.toImportExportAssetDto(any()))
            .thenReturn(importExportAssetDto);

        ImportExportProjectDto.ImportExportProjectGridDto
            importExportProjectGridDto = getImportExportProjectGridDto();

        when(projectGridMapper.toImportExportProjectGridDto(any()))
            .thenReturn(importExportProjectGridDto);

        ImportExportProjectDto actual = projectMapper.toImportExportProjectDto(project);

        assertThat(actual.getName()).isEqualTo("name");
        assertThat(actual.getToken()).isEqualTo("token");
        assertThat(actual.isDisplayProgressBar()).isTrue();
        Assertions.assertThat(actual.getGridProperties().getMaxColumn()).isEqualTo(1);
        Assertions.assertThat(actual.getGridProperties().getWidgetHeight()).isEqualTo(1);
        Assertions.assertThat(actual.getGridProperties().getCssStyle()).isEqualTo("style");
        assertThat(actual.getImage().getContent()).isEqualTo(new byte[10]);
        assertThat(actual.getImage().getContentType()).isEqualTo("contentType");
        assertThat(actual.getImage().getSize()).isEqualTo(10);
        assertThat(actual.getGrids().get(0).getId()).isEqualTo(1L);
        assertThat(actual.getGrids().get(0).getTime()).isEqualTo(10);
        assertThat(actual.getGrids().get(0).getWidgets().get(0).getId()).isEqualTo(1L);
    }

    @Test
    void shouldToProjectEntityImportExportProjectDto() {
        GridPropertiesResponseDto gridPropertiesResponseDto = new GridPropertiesResponseDto();
        gridPropertiesResponseDto.setCssStyle("style");
        gridPropertiesResponseDto.setMaxColumn(1);
        gridPropertiesResponseDto.setWidgetHeight(1);

        ImportExportAssetDto importExportAssetDto = new ImportExportAssetDto();
        importExportAssetDto.setSize(10);
        importExportAssetDto.setContentType("contentType");
        importExportAssetDto.setContent(new byte[10]);

        ProjectWidget projectWidget = new ProjectWidget();
        projectWidget.setId(1L);

        ProjectGrid projectGrid = new ProjectGrid();
        projectGrid.setId(1L);
        projectGrid.setTime(10);
        projectGrid.setWidgets(Collections.singleton(projectWidget));

        Asset asset = new Asset();
        asset.setSize(10);
        asset.setContentType("contentType");
        asset.setContent(new byte[10]);

        when(assetMapper.toAssetEntity(any()))
            .thenReturn(asset);
        when(projectGridMapper.toProjectGridEntity(any(ImportExportProjectDto.ImportExportProjectGridDto.class)))
            .thenReturn(projectGrid);

        ImportExportProjectDto importExportProjectDto =
            getImportExportProjectDto(importExportAssetDto, gridPropertiesResponseDto);

        Project actual = projectMapper.toProjectEntity(importExportProjectDto);

        assertThat(actual.getName()).isEqualTo("name");
        assertThat(actual.getToken()).isEqualTo("token");
        assertThat(actual.getWidgetHeight()).isEqualTo(1);
        assertThat(actual.getMaxColumn()).isEqualTo(1);
        assertThat(actual.isDisplayProgressBar()).isTrue();
        assertThat(actual.getScreenshot().getSize()).isEqualTo(10);
        assertThat(actual.getScreenshot().getContent()).isEqualTo(new byte[10]);
        assertThat(actual.getScreenshot().getContentType()).isEqualTo("contentType");
        assertThat(new ArrayList<>(actual.getGrids()).get(0).getId()).isEqualTo(1L);
        assertThat(new ArrayList<>(actual.getGrids()).get(0).getTime()).isEqualTo(10);
        assertThat(new ArrayList<>(new ArrayList<>(actual.getGrids()).get(0).getWidgets()).get(0).getId()).isEqualTo(
            1L);
    }
}
