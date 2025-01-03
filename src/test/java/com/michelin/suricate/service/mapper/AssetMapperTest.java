package com.michelin.suricate.service.mapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.michelin.suricate.model.dto.api.asset.AssetResponseDto;
import com.michelin.suricate.model.dto.api.export.ImportExportAssetDto;
import com.michelin.suricate.model.entity.Asset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssetMapperTest {
    @InjectMocks
    private AssetMapperImpl assetMapper;

    @Test
    void shouldToAssetDto() {
        Asset asset = new Asset();
        asset.setId(1L);
        asset.setSize(10);
        asset.setContentType("contentType");
        asset.setContent(new byte[10]);

        AssetResponseDto actual = assetMapper.toAssetDto(asset);

        assertEquals(1L, actual.getId());
        assertEquals(10, actual.getSize());
        assertArrayEquals(new byte[10], actual.getContent());
        assertEquals("contentType", actual.getContentType());
    }

    @Test
    void shouldToImportExportAssetDto() {
        Asset asset = new Asset();
        asset.setId(1L);
        asset.setSize(10);
        asset.setContentType("contentType");
        asset.setContent(new byte[10]);

        ImportExportAssetDto actual = assetMapper.toImportExportAssetDto(asset);

        assertEquals(10, actual.getSize());
        assertArrayEquals(new byte[10], actual.getContent());
        assertEquals("contentType", actual.getContentType());
    }

    @Test
    void shouldToAssetEntity() {
        ImportExportAssetDto asset = new ImportExportAssetDto();
        asset.setSize(10);
        asset.setContentType("contentType");
        asset.setContent(new byte[10]);

        Asset actual = assetMapper.toAssetEntity(asset);

        assertEquals(10, actual.getSize());
        assertArrayEquals(new byte[10], actual.getContent());
        assertEquals("contentType", actual.getContentType());
    }
}
