/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.michelin.suricate.controller;

import com.michelin.suricate.model.dto.api.error.ApiErrorDto;
import com.michelin.suricate.model.entity.Asset;
import com.michelin.suricate.service.api.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

/**
 * Asset controller.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Asset", description = "Asset Controller")
public class AssetController {
    @Autowired
    private AssetService assetService;

    /**
     * Get asset for the specified token.
     *
     * @param token the asset token used to identify the asset
     * @return the asset data
     */
    @Operation(summary = "Get an asset by its token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "400", description = "Cannot decrypt token",
            content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))}),
        @ApiResponse(responseCode = "401", description = "Invalid token",
            content = {@Content(schema = @Schema(implementation = ApiErrorDto.class))})
    })
    @GetMapping(path = "/v1/assets/{token}/content")
    public ResponseEntity<byte[]> getAsset(@Parameter(hidden = true) WebRequest webRequest,
                                           @Parameter(name = "token", description = "The asset Token", required = true)
                                           @PathVariable("token") String token) {
        Asset asset = assetService.getAssetById(token);

        if (webRequest.checkNotModified(asset.getLastModifiedDate().getTime())) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.parseMediaType(asset.getContentType()))
            .contentLength(asset.getSize())
            .lastModified(asset.getLastModifiedDate().getTime())
            .cacheControl(CacheControl.noCache())
            .body(asset.getContent());
    }
}
