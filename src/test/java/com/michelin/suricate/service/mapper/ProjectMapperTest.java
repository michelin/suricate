package com.michelin.suricate.service.mapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import com.michelin.suricate.model.entity.Asset;
import com.michelin.suricate.model.entity.Project;
import com.michelin.suricate.model.entity.ProjectGrid;
import com.michelin.suricate.model.entity.ProjectWidget;
import com.michelin.suricate.service.api.LibraryService;
import com.michelin.suricate.util.IdUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

            assertEquals("token", actual.getToken());
            assertEquals("name", actual.getName());
            assertEquals("encrypted", actual.getScreenshotToken());
            assertEquals("libraryToken", actual.getLibrariesToken().getFirst());
            assertEquals(1, actual.getGridProperties().getMaxColumn());
            assertEquals(1, actual.getGridProperties().getWidgetHeight());
            assertEquals("style", actual.getGridProperties().getCssStyle());
            assertEquals(1L, actual.getImage().getId());
            assertArrayEquals(new byte[10], actual.getImage().getContent());
            assertEquals(10, actual.getImage().getSize());
            assertEquals("contentType", actual.getImage().getContentType());
            assertEquals(1L, actual.getGrids().getFirst().getId());
            assertEquals(10, actual.getGrids().getFirst().getTime());
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

            assertEquals("token", actual.getToken());
            assertEquals("name", actual.getName());
            assertEquals("encrypted", actual.getScreenshotToken());
            assertTrue(actual.getLibrariesToken().isEmpty());
            assertEquals(1, actual.getGridProperties().getMaxColumn());
            assertEquals(1, actual.getGridProperties().getWidgetHeight());
            assertEquals("style", actual.getGridProperties().getCssStyle());
            assertEquals(1L, actual.getImage().getId());
            assertArrayEquals(new byte[10], actual.getImage().getContent());
            assertEquals(10, actual.getImage().getSize());
            assertEquals("contentType", actual.getImage().getContentType());
            assertEquals(1L, actual.getGrids().getFirst().getId());
            assertEquals(10, actual.getGrids().getFirst().getTime());
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

        assertEquals("token", actual.getToken());
        assertEquals("name", actual.getName());
        assertNull(actual.getScreenshotToken());
        assertTrue(actual.getLibrariesToken().isEmpty());
        assertEquals(1, actual.getGridProperties().getMaxColumn());
        assertEquals(1, actual.getGridProperties().getWidgetHeight());
        assertEquals("style", actual.getGridProperties().getCssStyle());
        assertNull(actual.getImage());
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

            assertEquals("token", actual.getFirst().getToken());
            assertEquals("name", actual.getFirst().getName());
            assertEquals("encrypted", actual.getFirst().getScreenshotToken());
            assertTrue(actual.getFirst().getLibrariesToken().isEmpty());
            assertEquals(1, actual.getFirst().getGridProperties().getMaxColumn());
            assertEquals(1, actual.getFirst().getGridProperties().getWidgetHeight());
            assertEquals("style", actual.getFirst().getGridProperties().getCssStyle());
            assertEquals(1L, actual.getFirst().getImage().getId());
            assertArrayEquals(new byte[10], actual.getFirst().getImage().getContent());
            assertEquals(10, actual.getFirst().getImage().getSize());
            assertEquals("contentType", actual.getFirst().getImage().getContentType());
            assertEquals(1L, actual.getFirst().getGrids().getFirst().getId());
            assertEquals(10, actual.getFirst().getGrids().getFirst().getTime());
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

        assertEquals("name", actual.getName());
        assertEquals("style", actual.getCssStyle());
        assertEquals(1, actual.getMaxColumn());
        assertEquals(1, actual.getWidgetHeight());
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

        assertEquals("token", actual.getToken());
        assertEquals("name", actual.getName());
        assertTrue(actual.isDisplayProgressBar());
        assertEquals(1, actual.getGridProperties().getMaxColumn());
        assertEquals(1, actual.getGridProperties().getWidgetHeight());
        assertEquals("style", actual.getGridProperties().getCssStyle());
        assertArrayEquals(new byte[10], actual.getImage().getContent());
        assertEquals(10, actual.getImage().getSize());
        assertEquals("contentType", actual.getImage().getContentType());
        assertEquals(1L, actual.getGrids().getFirst().getId());
        assertEquals(10, actual.getGrids().getFirst().getTime());
        assertEquals(1L, actual.getGrids().getFirst().getWidgets().getFirst().getId());
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

        assertEquals("token", actual.getToken());
        assertEquals("name", actual.getName());
        assertEquals(1, actual.getWidgetHeight());
        assertEquals(1, actual.getMaxColumn());
        assertTrue(actual.isDisplayProgressBar());
        assertEquals(10, actual.getScreenshot().getSize());
        assertArrayEquals(new byte[10], actual.getScreenshot().getContent());
        assertEquals("contentType", actual.getScreenshot().getContentType());
        assertEquals(1L, new ArrayList<>(actual.getGrids()).getFirst().getId());
        assertEquals(10, new ArrayList<>(actual.getGrids()).getFirst().getTime());
        assertEquals(1L, new ArrayList<>(new ArrayList<>(actual.getGrids())
            .getFirst()
            .getWidgets())
            .getFirst()
            .getId());
    }
}
