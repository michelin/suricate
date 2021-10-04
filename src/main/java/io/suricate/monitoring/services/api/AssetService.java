/*
 * Copyright 2012-2021 the original author or authors.
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
import io.suricate.monitoring.utils.IdUtils;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
     * @param token the asset token used to identify the asset
     * @return The related asset
     */
    public Asset getAssetById(final String token) {
        Optional<Asset> assetOptional = assetRepository.findById(IdUtils.decrypt(token));

        if (!assetOptional.isPresent()) {
            throw new ObjectNotFoundException(Asset.class, token);
        }

        return assetOptional.get();
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
