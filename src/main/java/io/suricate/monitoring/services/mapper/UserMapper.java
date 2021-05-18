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

package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.configuration.security.ConnectedUser;
import io.suricate.monitoring.model.dto.api.user.UserRequestDto;
import io.suricate.monitoring.model.dto.api.user.UserResponseDto;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.model.enums.AuthenticationMethod;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Manage the generation DTO/Model objects for User class
 */
@Component
@Mapper(
    componentModel = "spring",
    uses = {
        RoleMapper.class
    }
)
public abstract class UserMapper {

    /**
     * The password encoder
     */
    @Autowired
    protected PasswordEncoder passwordEncoder;

    /**
     * Map a user into a DTO
     *
     * @param user The user to map
     * @return The user as DTO
     */
    @Named("toUserDTO")
    @Mapping(target = "fullname", expression = "java(String.format(\"%s %s\", user.getFirstname(), user.getLastname()))")
    @Mapping(target = "roles", qualifiedByName = "toRoleDTO")
    public abstract UserResponseDto toUserDTO(User user);

    /**
     * Map a list of users into a list of users as DTOs
     *
     * @param users The list of user to map
     * @return The users as DTO
     */
    @Named("toUsersDTOs")
    @IterableMapping(qualifiedByName = "toUserDTO")
    public abstract List<UserResponseDto> toUsersDTOs(Collection<User> users);

    /**
     * Map a connected user to user entity
     *
     * @param connectedUser The connected user to map
     * @return The user entity
     */
    @Named("connectedUserToUserEntity")
    @Mapping(target = "authenticationMethod", expression = "java(AuthenticationMethod.LDAP)")
    @Mapping(target = "email", source = "connectedUser.mail")
    public abstract User connectedUserToUserEntity(final ConnectedUser connectedUser);

    /**
     * Map a user DTO into a user as entity
     *
     * @param userRequestDto       The userRequestDto to map
     * @param authenticationMethod The authentication method of the user
     * @return The user entity
     */
    @Named("toUserEntity")
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authenticationMethod", source = "authenticationMethod")
    @Mapping(target = "password", expression = "java( passwordEncoder.encode(userRequestDto.getPassword()) )")
    public abstract User toUserEntity(UserRequestDto userRequestDto, AuthenticationMethod authenticationMethod);
}
