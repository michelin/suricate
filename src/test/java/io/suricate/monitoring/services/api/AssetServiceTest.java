package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.entities.Asset;
import io.suricate.monitoring.repositories.AssetRepository;
import io.suricate.monitoring.utils.IdUtils;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

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

            verify(assetRepository, times(1))
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

        verify(assetRepository, times(1))
                .save(asset);
    }
}
