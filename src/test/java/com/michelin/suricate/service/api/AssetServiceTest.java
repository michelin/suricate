package com.michelin.suricate.service.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entity.Asset;
import com.michelin.suricate.repository.AssetRepository;
import com.michelin.suricate.util.IdUtils;
import com.michelin.suricate.util.exception.ObjectNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {
    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private AssetService assetService;

    @Test
    void shouldGetAssetById() {
        try (MockedStatic<IdUtils> mocked = mockStatic(IdUtils.class)) {
            Asset asset = new Asset();
            asset.setId(1L);
            asset.setContent(new byte[10]);
            asset.setSize(1L);
            asset.setContentType("contentType");

            mocked.when(() -> IdUtils.decrypt("token"))
                .thenReturn(1L);
            when(assetRepository.findById(1L))
                .thenReturn(Optional.of(asset));

            Asset actual = assetService.getAssetById("token");

            assertThat(actual)
                .isNotNull()
                .isEqualTo(asset);

            verify(assetRepository)
                .findById(1L);
        }
    }

    @Test
    void shouldGetAssetByIdNotFound() {
        try (MockedStatic<IdUtils> mocked = mockStatic(IdUtils.class)) {
            Asset asset = new Asset();
            asset.setId(1L);
            asset.setContent(new byte[10]);
            asset.setSize(1L);
            asset.setContentType("contentType");

            mocked.when(() -> IdUtils.decrypt("token"))
                .thenReturn(1L);
            when(assetRepository.findById(1L))
                .thenReturn(Optional.empty());

            assertThatThrownBy(() -> assetService.getAssetById("token"))
                .isInstanceOf(ObjectNotFoundException.class);
        }
    }

    @Test
    void shouldSave() {
        Asset asset = new Asset();
        asset.setId(1L);
        asset.setContent(new byte[10]);
        asset.setSize(1L);
        asset.setContentType("contentType");

        when(assetRepository.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));

        Asset actual = assetService.save(asset);

        assertThat(actual)
            .isNotNull()
            .isEqualTo(asset);

        verify(assetRepository)
            .save(asset);
    }
}
