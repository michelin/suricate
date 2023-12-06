package com.michelin.suricate.model.dto.api.token;

import com.michelin.suricate.model.dto.api.AbstractDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Jwt authentication response DTO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "JWT authentication response")
public class JwtAuthenticationResponseDto extends AbstractDto {
    @Schema(description = "The access token")
    private String accessToken;
}
