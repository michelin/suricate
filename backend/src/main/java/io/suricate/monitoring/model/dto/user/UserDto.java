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

package io.suricate.monitoring.model.dto.user;

import io.suricate.monitoring.model.dto.AbstractDto;
import io.suricate.monitoring.model.enums.AuthenticationMethod;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Reprensent a user used for communication with the clients via webservices
 */
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(callSuper = false) @ToString
public class UserDto extends AbstractDto {

    /**
     * Data base id
     */
    private Long id;

    /**
     * User firstname
     */
    private String firstname;

    /**
     * User lastname
     */
    private String lastname;

    /**
     * User fullname
     */
    private String fullname;

    /**
     *  username
     */
    private String username;

    /**
     *  Mail
     */
    private String email;

    /**
     * Password of the user
     */
    private String password;

    /**
     * The confirmation password
     */
    private String confirmPassword;

    /**
     * The authentication method
     */
    private AuthenticationMethod authenticationMethod;

    /**
     * User roles
     */
    private List<RoleDto> roles = new ArrayList<>();
}
