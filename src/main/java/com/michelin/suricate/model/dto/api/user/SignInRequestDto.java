package com.michelin.suricate.model.dto.api.user;

import com.michelin.suricate.model.dto.api.AbstractDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Sign in request")
public class SignInRequestDto extends AbstractDto {
    @NotBlank
    @Schema(description = "The username")
    private String username;

    @NotBlank
    @Schema(description = "The password")
    private String password;
}
