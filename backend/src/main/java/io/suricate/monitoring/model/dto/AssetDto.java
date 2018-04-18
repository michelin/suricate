package io.suricate.monitoring.model.dto;

import lombok.*;

/**
 * Asset class used for communicate through webservices
 */
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(callSuper = false) @ToString
public class AssetDto extends AbstractDto  {
    /**
     * The asset id
     */
    private Long id;

    /**
     * The blob content
     */
    private byte[] content;

    /**
     * The content type
     */
    private String contentType;

    /**
     * The size of the asset
     */
    private long size;
}
