/*
 * Copyright 2012-2018 the original author or authors.
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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent a role used for communication with the clients via webservices
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
@ApiModel(value = "Role", description = "Describe a user role")
public class RoleDto extends AbstractDto {

    /**
     * The role id
     */
    @ApiModelProperty(value = "The id")
    private Long id;
    /**
     * The role name
     */
    @ApiModelProperty(value = "The Role name")
    private String name;
    /**
     * The role description
     */
    @ApiModelProperty(value = "The description of the role")
    private String description;

    /**
     * The list of user for this role
     */
    @ApiModelProperty(value = "The list of users with this role", dataType = "java.util.List")
    private List<UserDto> users = new ArrayList<>();
}
