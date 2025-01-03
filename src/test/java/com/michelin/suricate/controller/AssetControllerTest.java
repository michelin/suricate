package com.michelin.suricate.controller;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entity.Asset;
import com.michelin.suricate.service.api.AssetService;
import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

@ExtendWith(MockitoExtension.class)
class AssetControllerTest {
    @Mock
    private AssetService assetService;

    @InjectMocks
    private AssetController assetController;

    @Test
    void shouldGetAssetNotModified() {
        Asset asset = new Asset();
        asset.setContentType("application/javascript");
        asset.setLastModifiedDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        asset.setContent(new byte[10]);

        WebRequest webRequest = mock(WebRequest.class);

        when(assetService.getAssetById(any()))
            .thenReturn(asset);
        when(webRequest.checkNotModified(anyLong()))
            .thenReturn(true);

        ResponseEntity<byte[]> actual = assetController.getAsset(webRequest, "token");

        assertEquals(HttpStatus.NOT_MODIFIED, actual.getStatusCode());

        verify(assetService)
            .getAssetById("token");
        verify(webRequest)
            .checkNotModified(946688400000L);
    }

    @Test
    void shouldGetAsset() {
        Asset asset = new Asset();
        asset.setContentType(MediaType.APPLICATION_JSON_VALUE);
        asset.setLastModifiedDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
        asset.setContent(new byte[10]);

        WebRequest webRequest = mock(WebRequest.class);

        when(assetService.getAssetById(any()))
            .thenReturn(asset);
        when(webRequest.checkNotModified(anyLong()))
            .thenReturn(false);

        ResponseEntity<byte[]> actual = assetController.getAsset(webRequest, "token");

        assertEquals(MediaType.APPLICATION_JSON, actual.getHeaders().getContentType());
        assertEquals(10L, actual.getHeaders().getContentLength());
        assertEquals(946688400000L, actual.getHeaders().getLastModified());
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertArrayEquals(new byte[10], actual.getBody());

        verify(assetService)
            .getAssetById("token");
        verify(webRequest)
            .checkNotModified(946688400000L);
    }
}
