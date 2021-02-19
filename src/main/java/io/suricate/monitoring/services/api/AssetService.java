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

package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.entities.Asset;
import io.suricate.monitoring.repositories.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Manage the assets
 */
@Service
public class AssetService {

    /**
     * The asset repository
     */
    private final AssetRepository assetRepository;

    /**
     * Constructor
     *
     * @param assetRepository The asset repository
     */
    @Autowired
    public AssetService(final AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    /**
     * Find an asset by ID
     *
     * @param id The asset id
     * @return The related asset
     */
    public Asset findOne(final Long id) {
        return assetRepository.getOne(id);
    }

    /**
     * Save an asset
     *
     * @param asset The asset to save
     * @return The saved asset
     */
    public Asset save(Asset asset) {
        return assetRepository.save(asset);
    }
}
