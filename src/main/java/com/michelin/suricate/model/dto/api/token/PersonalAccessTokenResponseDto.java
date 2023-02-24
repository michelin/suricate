package com.michelin.suricate.model.dto.api.token;

import com.michelin.suricate.model.dto.api.AbstractDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Describe a personal access token response")
public class PersonalAccessTokenResponseDto extends AbstractDto {
    @Schema(description = "The token name")
    private String name;

    @Schema(description = "The token value")
    private String value;

    @Schema(description = "The token creation date")
    private Date createdDate;
}
