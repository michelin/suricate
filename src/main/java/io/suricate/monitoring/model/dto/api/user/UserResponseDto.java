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
import io.suricate.monitoring.model.dto.api.role.RoleResponseDto;
import io.suricate.monitoring.model.enums.AuthenticationProvider;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represent a user used for communication with the clients via webservices
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "UserResponse", description = "Describe a user of the app")
public class UserResponseDto extends AbstractDto {

    /**
     * Database id
     */
    @ApiModelProperty(value = "The id of the user")
    private Long id;

    /**
     * User firstname
     */
    @ApiModelProperty(value = "The firstname of the user")
    private String firstname;

    /**
     * User lastname
     */
    @ApiModelProperty(value = "The lastname of the user")
    private String lastname;

    /**
     * User fullname
     */
    @ApiModelProperty(value = "The fullname of the user")
    private String fullname;

    /**
     * username
     */
    @ApiModelProperty(value = "The username used as login in the credentials")
    private String username;

    /**
     * Mail
     */
    @ApiModelProperty(value = "The user email")
    private String email;

    /**
     * The authentication method
     */
    @ApiModelProperty(value = "The authentication method for this user")
    private AuthenticationProvider authenticationMethod;

    /**
     * The list of roles for this user
     */
    @ApiModelProperty(value = "The list of roles for this user")
    private List<RoleResponseDto> roles;
}
