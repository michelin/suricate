package io.suricate.monitoring.model.dto.api.token;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@ApiModel(value = "JwtAuthenticationResponse", description = "JWT authentication response")
@AllArgsConstructor
public class JwtAuthenticationResponseDto {
    /**
     * The JWT self-generated access token
     */
    @ApiModelProperty(value = "The access token")
    private String accessToken;
}
