package com.michelin.suricate.services.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.michelin.suricate.model.dto.api.asset.AssetResponseDto;
import com.michelin.suricate.model.dto.api.export.ImportExportAssetDto;
import com.michelin.suricate.model.entities.Asset;
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

        assertThat(actual.getId()).isEqualTo(1L);
        assertThat(actual.getSize()).isEqualTo(10);
        assertThat(actual.getContent()).isEqualTo(new byte[10]);
        assertThat(actual.getContentType()).isEqualTo("contentType");
    }

    @Test
    void shouldToImportExportAssetDto() {
        Asset asset = new Asset();
        asset.setId(1L);
        asset.setSize(10);
        asset.setContentType("contentType");
        asset.setContent(new byte[10]);

        ImportExportAssetDto actual = assetMapper.toImportExportAssetDto(asset);

        assertThat(actual.getSize()).isEqualTo(10);
        assertThat(actual.getContent()).isEqualTo(new byte[10]);
        assertThat(actual.getContentType()).isEqualTo("contentType");
    }

    @Test
    void shouldToAssetEntity() {
        ImportExportAssetDto asset = new ImportExportAssetDto();
        asset.setSize(10);
        asset.setContentType("contentType");
        asset.setContent(new byte[10]);

        Asset actual = assetMapper.toAssetEntity(asset);

        assertThat(actual.getSize()).isEqualTo(10);
        assertThat(actual.getContent()).isEqualTo(new byte[10]);
        assertThat(actual.getContentType()).isEqualTo("contentType");
    }
}
