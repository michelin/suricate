/*
 *
 *  * Copyright 2012-2018 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package io.suricate.monitoring.controllers;

import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.entities.Asset;
import io.suricate.monitoring.services.api.AssetService;
import io.suricate.monitoring.utils.IdUtils;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import io.swagger.annotations.*;
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
import springfox.documentation.annotations.ApiIgnore;

import java.util.Optional;

/**
 * Asset controller
 */
@RestController
@RequestMapping("/api")
@Api(value = "Asset Controller", tags = {"Assets"})
public class AssetController {

    /**
     * Asset Service
     */
    private final AssetService assetService;

    /**
     * The constructor
     *
     * @param assetService The asset service
     */
    @Autowired
    public AssetController(final AssetService assetService) {
        this.assetService = assetService;
    }

    /**
     * Get asset for the specified token
     *
     * @param token the asset token used to identify the asset
     * @return the asset data
     */
    @ApiOperation(value = "Get an asset by its token", response = byte.class)
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok"),
        @ApiResponse(code = 400, response = ApiErrorDto.class, message = "Cannot decrypt token"),
        @ApiResponse(code = 401, response = ApiErrorDto.class, message = "Invalid token")
    })
    @GetMapping(path = "/v1/assets/{token}/content")
    public ResponseEntity<byte[]> getAsset(@ApiIgnore WebRequest webRequest,
                                           @ApiParam(name = "token", value = "The asset Token", required = true)
                                           @PathVariable("token") String token) {
        Optional<Asset> asset = assetService.getAssetById(IdUtils.decrypt(token));

        if (!asset.isPresent()) {
            throw new ObjectNotFoundException(Asset.class, token);
        }

        if (webRequest.checkNotModified(asset.get().getLastModifiedDate().getTime())) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.parseMediaType(asset.get().getContentType()))
            .contentLength(asset.get().getSize())
            .lastModified(asset.get().getLastModifiedDate().getTime())
            .cacheControl(CacheControl.noCache())
            .body(asset.get().getContent());
    }
}
