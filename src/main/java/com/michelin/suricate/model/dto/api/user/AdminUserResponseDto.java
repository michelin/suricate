package com.michelin.suricate.model.dto.api.user;

import com.michelin.suricate.model.dto.api.role.RoleResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Describe a user with data for admins")
public class AdminUserResponseDto extends UserResponseDto {
    @Schema(description = "The id of the user", example = "1")
    private Long id;

    @Schema(description = "The user email")
    private String email;

    @Schema(description = "The list of roles for this user")
    private List<RoleResponseDto> roles;
}
