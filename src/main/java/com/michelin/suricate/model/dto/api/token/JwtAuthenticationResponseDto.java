package com.michelin.suricate.model.dto.api.token;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Schema(description = "JWT authentication response")
@AllArgsConstructor
public class JwtAuthenticationResponseDto {
    @Schema(description = "The access token")
    private String accessToken;
}
