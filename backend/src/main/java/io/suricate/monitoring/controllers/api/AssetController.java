/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.suricate.monitoring.controllers.api;

import io.suricate.monitoring.model.entity.Asset;
import io.suricate.monitoring.service.api.AssetService;
import io.suricate.monitoring.utils.IdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

/**
 * Asset controller
 */
@RestController
@RequestMapping("/api/asset")
public class AssetController {

    /**
     * Class logger
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(AssetController.class);

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
     * @param token the asset token used to identify the asset
     * @return the asset data
     */
    @RequestMapping(path = "/{token}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getAsset(WebRequest webRequest, @PathVariable("token") String token) {
        Asset asset = assetService.findOne(IdUtils.decrypt(token));
        if (asset == null){
            return ResponseEntity.notFound().build();
        } else if (webRequest.checkNotModified(asset.getLastModifiedDate().getTime())){
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
