package io.suricate.monitoring.model.dto.api.token;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Describe a personal access token request")
public class PersonalAccessTokenRequestDto extends AbstractDto {
    @Schema(description = "The name")
    private String name;
}
