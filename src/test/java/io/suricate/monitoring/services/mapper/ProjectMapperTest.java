package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.asset.AssetResponseDto;
import io.suricate.monitoring.model.dto.api.export.ImportExportAssetDto;
import io.suricate.monitoring.model.dto.api.export.ImportExportProjectDto;
import io.suricate.monitoring.model.dto.api.project.GridPropertiesResponseDto;
import io.suricate.monitoring.model.dto.api.project.ProjectRequestDto;
import io.suricate.monitoring.model.dto.api.project.ProjectResponseDto;
import io.suricate.monitoring.model.dto.api.projectgrid.ProjectGridResponseDto;
import io.suricate.monitoring.model.entities.*;
import io.suricate.monitoring.services.api.LibraryService;
import io.suricate.monitoring.utils.IdUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

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

    @Test
    void shouldToProjectDTO() {
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
            when(assetMapper.toAssetDTO(any()))
                    .thenReturn(image);
            when(projectGridMapper.toProjectGridDTO(any()))
                    .thenReturn(grid);

            ProjectResponseDto actual = projectMapper.toProjectDTO(project);

            assertThat(actual.getToken()).isEqualTo("token");
            assertThat(actual.getName()).isEqualTo("name");
            assertThat(actual.getScreenshotToken()).isEqualTo("encrypted");
            assertThat(actual.getLibrariesToken().get(0)).isEqualTo("libraryToken");
            assertThat(actual.getGridProperties().getMaxColumn()).isEqualTo(1);
            assertThat(actual.getGridProperties().getWidgetHeight()).isEqualTo(1);
            assertThat(actual.getGridProperties().getCssStyle()).isEqualTo("style");
            assertThat(actual.getImage().getId()).isEqualTo(1L);
            assertThat(actual.getImage().getContent()).isEqualTo(new byte[10]);
            assertThat(actual.getImage().getSize()).isEqualTo(10);
            assertThat(actual.getImage().getContentType()).isEqualTo("contentType");
            assertThat(actual.getGrids().get(0).getId()).isEqualTo(1L);
            assertThat(actual.getGrids().get(0).getTime()).isEqualTo(10);
        }
    }

    @Test
    void shouldToProjectDTONoLibrary() {
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
            when(assetMapper.toAssetDTO(any()))
                    .thenReturn(image);
            when(projectGridMapper.toProjectGridDTO(any()))
                    .thenReturn(grid);

            ProjectResponseDto actual = projectMapper.toProjectDTONoLibrary(project);

            assertThat(actual.getToken()).isEqualTo("token");
            assertThat(actual.getName()).isEqualTo("name");
            assertThat(actual.getLibrariesToken()).isEmpty();
            assertThat(actual.getScreenshotToken()).isEqualTo("encrypted");
            assertThat(actual.getGridProperties().getMaxColumn()).isEqualTo(1);
            assertThat(actual.getGridProperties().getWidgetHeight()).isEqualTo(1);
            assertThat(actual.getGridProperties().getCssStyle()).isEqualTo("style");
            assertThat(actual.getImage().getId()).isEqualTo(1L);
            assertThat(actual.getImage().getContent()).isEqualTo(new byte[10]);
            assertThat(actual.getImage().getSize()).isEqualTo(10);
            assertThat(actual.getImage().getContentType()).isEqualTo("contentType");
            assertThat(actual.getGrids().get(0).getId()).isEqualTo(1L);
            assertThat(actual.getGrids().get(0).getTime()).isEqualTo(10);
        }
    }

    @Test
    void shouldToProjectDTONoAsset() {
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

        when(projectGridMapper.toProjectGridDTO(any()))
                .thenReturn(grid);

        ProjectResponseDto actual = projectMapper.toProjectDTONoAsset(project);

        assertThat(actual.getToken()).isEqualTo("token");
        assertThat(actual.getName()).isEqualTo("name");
        assertThat(actual.getLibrariesToken()).isEmpty();
        assertThat(actual.getScreenshotToken()).isNull();
        assertThat(actual.getGridProperties().getMaxColumn()).isEqualTo(1);
        assertThat(actual.getGridProperties().getWidgetHeight()).isEqualTo(1);
        assertThat(actual.getGridProperties().getCssStyle()).isEqualTo("style");
        assertThat(actual.getImage()).isNull();
    }

    @Test
    void shouldToProjectsDTOs() {
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
            when(assetMapper.toAssetDTO(any()))
                    .thenReturn(image);
            when(projectGridMapper.toProjectGridDTO(any()))
                    .thenReturn(grid);

            List<ProjectResponseDto> actual = projectMapper.toProjectsDTOs(Collections.singletonList(project));

            assertThat(actual.get(0).getToken()).isEqualTo("token");
            assertThat(actual.get(0).getName()).isEqualTo("name");
            assertThat(actual.get(0).getLibrariesToken()).isEmpty();
            assertThat(actual.get(0).getScreenshotToken()).isEqualTo("encrypted");
            assertThat(actual.get(0).getGridProperties().getMaxColumn()).isEqualTo(1);
            assertThat(actual.get(0).getGridProperties().getWidgetHeight()).isEqualTo(1);
            assertThat(actual.get(0).getGridProperties().getCssStyle()).isEqualTo("style");
            assertThat(actual.get(0).getImage().getId()).isEqualTo(1L);
            assertThat(actual.get(0).getImage().getContent()).isEqualTo(new byte[10]);
            assertThat(actual.get(0).getImage().getSize()).isEqualTo(10);
            assertThat(actual.get(0).getImage().getContentType()).isEqualTo("contentType");
            assertThat(actual.get(0).getGrids().get(0).getId()).isEqualTo(1L);
            assertThat(actual.get(0).getGrids().get(0).getTime()).isEqualTo(10);
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
    void shouldToImportExportProjectDTO() {
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

        ImportExportProjectDto.ImportExportProjectGridDto.ImportExportProjectWidgetDto importExportProjectWidgetDto = new ImportExportProjectDto.ImportExportProjectGridDto.ImportExportProjectWidgetDto();
        importExportProjectWidgetDto.setId(1L);

        ImportExportProjectDto.ImportExportProjectGridDto importExportProjectGridDto = new ImportExportProjectDto.ImportExportProjectGridDto();
        importExportProjectGridDto.setId(1L);
        importExportProjectGridDto.setTime(10);
        importExportProjectGridDto.setWidgets(Collections.singletonList(importExportProjectWidgetDto));

        ImportExportAssetDto importExportAssetDto = new ImportExportAssetDto();
        importExportAssetDto.setSize(10);
        importExportAssetDto.setContentType("contentType");
        importExportAssetDto.setContent(new byte[10]);

        when(assetMapper.toImportExportAssetDTO(any()))
                .thenReturn(importExportAssetDto);
        when(projectGridMapper.toImportExportProjectGridDTO(any()))
                .thenReturn(importExportProjectGridDto);

        ImportExportProjectDto actual = projectMapper.toImportExportProjectDTO(project);

        assertThat(actual.getName()).isEqualTo("name");
        assertThat(actual.getToken()).isEqualTo("token");
        assertThat(actual.isDisplayProgressBar()).isTrue();
        assertThat(actual.getGridProperties().getMaxColumn()).isEqualTo(1);
        assertThat(actual.getGridProperties().getWidgetHeight()).isEqualTo(1);
        assertThat(actual.getGridProperties().getCssStyle()).isEqualTo("style");
        assertThat(actual.getImage().getContent()).isEqualTo(new byte[10]);
        assertThat(actual.getImage().getContentType()).isEqualTo("contentType");
        assertThat(actual.getImage().getSize()).isEqualTo(10);
        assertThat(actual.getGrids().get(0).getId()).isEqualTo(1L);
        assertThat(actual.getGrids().get(0).getTime()).isEqualTo(10);
        assertThat(actual.getGrids().get(0).getWidgets().get(0).getId()).isEqualTo(1L);
    }

    @Test
    void shouldToProjectEntityImportExportProjectDto() {
        ImportExportProjectDto.ImportExportProjectGridDto.ImportExportProjectWidgetDto importExportProjectWidgetDto = new ImportExportProjectDto.ImportExportProjectGridDto.ImportExportProjectWidgetDto();
        importExportProjectWidgetDto.setId(1L);

        ImportExportProjectDto.ImportExportProjectGridDto importExportProjectGridDto = new ImportExportProjectDto.ImportExportProjectGridDto();
        importExportProjectGridDto.setId(1L);
        importExportProjectGridDto.setTime(10);
        importExportProjectGridDto.setWidgets(Collections.singletonList(importExportProjectWidgetDto));

        GridPropertiesResponseDto gridPropertiesResponseDto = new GridPropertiesResponseDto();
        gridPropertiesResponseDto.setCssStyle("style");
        gridPropertiesResponseDto.setMaxColumn(1);
        gridPropertiesResponseDto.setWidgetHeight(1);

        ImportExportAssetDto importExportAssetDto = new ImportExportAssetDto();
        importExportAssetDto.setSize(10);
        importExportAssetDto.setContentType("contentType");
        importExportAssetDto.setContent(new byte[10]);

        ImportExportProjectDto importExportProjectDto = new ImportExportProjectDto();
        importExportProjectDto.setName("name");
        importExportProjectDto.setToken("token");
        importExportProjectDto.setDisplayProgressBar(true);
        importExportProjectDto.setImage(importExportAssetDto);
        importExportProjectDto.setGridProperties(gridPropertiesResponseDto);
        importExportProjectDto.setGrids(Collections.singletonList(importExportProjectGridDto));

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
        assertThat(new ArrayList<>(new ArrayList<>(actual.getGrids()).get(0).getWidgets()).get(0).getId()).isEqualTo(1L);
    }
}
