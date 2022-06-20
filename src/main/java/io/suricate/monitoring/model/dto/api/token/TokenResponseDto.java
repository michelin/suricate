package io.suricate.monitoring.model.dto.api.token;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "TokenResponse", description = "Describe a token")
public class TokenResponseDto extends AbstractDto {
    /**
     * The token name
     */
    @ApiModelProperty(value = "The token name")
    private String name;

    /**
     * The token value
     */
    @ApiModelProperty(value = "The token value")
    private String value;

    /**
     * The token creation date
     */
    @ApiModelProperty(value = "The token creation date")
    private Date createdDate;
}
