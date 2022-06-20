package io.suricate.monitoring.model.dto.api.token;

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
public class TokenRequestDto extends AbstractDto {
    /**
     * The name
     */
    @ApiModelProperty(value = "The name")
    private String name;
}
