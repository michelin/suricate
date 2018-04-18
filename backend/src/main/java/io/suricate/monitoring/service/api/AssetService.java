package io.suricate.monitoring.service.api;

import io.suricate.monitoring.model.entity.Asset;
import io.suricate.monitoring.repository.AssetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Manage the assets
 */
@Service
public class AssetService {

    /**
     * Class logger
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(AssetService.class);

    /**
     * The asset repository
     */
    private final AssetRepository assetRepository;

    /**
     * Constructor
     *
     * @param assetRepository The asset repository to inject
     */
    @Autowired
    public AssetService(final AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    /**
     * Find an asset by id
     *
     * @param id The asset id
     * @return The related asset
     */
    public Asset findOne(final Long id) {
        return assetRepository.findOne(id);
    }

    /**
     * Save a new asset in database
     *
     * @param asset The asset to save
     * @return The asset saved
     */
    public Asset save(Asset asset) {
        return assetRepository.save(asset);
    }
}
