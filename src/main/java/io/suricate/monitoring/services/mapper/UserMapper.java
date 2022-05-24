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

package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.configuration.security.common.ConnectedUser;
import io.suricate.monitoring.model.dto.api.user.UserRequestDto;
import io.suricate.monitoring.model.dto.api.user.UserResponseDto;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.model.enums.AuthenticationMethod;
import io.suricate.monitoring.model.enums.UserRoleEnum;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manage the generation DTO/Model objects for User class
 */
@Component
@Mapper(
    componentModel = "spring",
    uses = {
        RoleMapper.class
    },
    imports = {
        Collection.class,
        Collectors.class,
        UserRoleEnum.class
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
     * @param user The user to map
     * @return The user as DTO
     */
    @Named("toUserDTO")
    @Mapping(target = "fullname", expression = "java(String.format(\"%s %s\", user.getFirstname(), user.getLastname()))")
    @Mapping(target = "admin", expression = "java(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()).contains(UserRoleEnum.ROLE_ADMIN.name()))")
    @Mapping(target = "roles", qualifiedByName = "toRoleDTO")
    public abstract UserResponseDto toUserDTO(User user);

    /**
     * Map a list of users into a list of users as DTOs
     * @param users The list of user to map
     * @return The users as DTO
     */
    @Named("toUsersDTOs")
    @IterableMapping(qualifiedByName = "toUserDTO")
    public abstract List<UserResponseDto> toUsersDTOs(Collection<User> users);

    /**
     * Map a connected user to user entity
     * @param username The username
     * @param firstname The user firstname
     * @param lastname The user lastname
     * @param email The user email
     * @param authenticationMethod The ID provider used
     * @return The user entity
     */
    @Named("connectedUserToUserEntity")
    public abstract User connectedUserToUserEntity(String username, String firstname, String lastname, String email, AuthenticationMethod authenticationMethod);

    /**
     * Map a user DTO into a user as entity
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
