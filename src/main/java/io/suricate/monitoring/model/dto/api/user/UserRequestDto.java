/*
 * Copyright 2012-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.suricate.monitoring.model.dto.api.user;

import io.suricate.monitoring.model.dto.api.AbstractDto;
import io.suricate.monitoring.model.enums.UserRoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Schema(description = "Create/Update a user")
public class UserRequestDto extends AbstractDto {
    @Schema(description = "The firstname of the user")
    private String firstname;

    @Schema(description = "The lastname of the user")
    private String lastname;

    @Schema(description = "The username used as login in the credentials")
    private String username;

    @Schema(description = "The user password")
    private String password;

    @Schema(description = "The user password confirmation")
    private String confirmPassword;

    @Schema(description = "The user email")
    private String email;

    @Schema(description = "The list of related roles", type = "java.util.List")
    private List<UserRoleEnum> roles = new ArrayList<>();
}
