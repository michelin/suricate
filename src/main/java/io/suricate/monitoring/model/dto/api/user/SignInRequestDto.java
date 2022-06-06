package io.suricate.monitoring.model.dto.api.user;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "SignInRequest", description = "Sign in request")
public class SignInRequestDto extends AbstractDto {
    /**
     * The username
     */
    @ApiModelProperty(value = "The username")
    private String username;

    /**
     * The password
     */
    @ApiModelProperty(value = "The password")
    private String password;
}
