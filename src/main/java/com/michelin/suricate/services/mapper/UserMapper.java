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

package com.michelin.suricate.services.mapper;

import com.michelin.suricate.model.dto.api.user.AdminUserResponseDto;
import com.michelin.suricate.model.dto.api.user.UserRequestDto;
import com.michelin.suricate.model.dto.api.user.UserResponseDto;
import com.michelin.suricate.model.entities.User;
import com.michelin.suricate.model.enums.AuthenticationProvider;
import java.util.Collection;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * User mapper.
 */
@Component
@Mapper(
    componentModel = "spring",
    uses = {
        RoleMapper.class
    },
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class UserMapper {
    @Autowired
    protected PasswordEncoder passwordEncoder;

    /**
     * Map a user into a DTO for admins.
     *
     * @param user The user to map
     * @return The user as DTO
     */
    @Named("toAdminUserDto")
    @Mapping(target = "roles", qualifiedByName = "toRoleDto")
    public abstract AdminUserResponseDto toAdminUserDto(User user);

    /**
     * Map a user into a DTO.
     *
     * @param user The user to map
     * @return The user as DTO
     */
    @Named("toUserDto")
    public abstract UserResponseDto toUserDto(User user);

    /**
     * Map a list of users into a list of users as DTOs.
     *
     * @param users The list of user to map
     * @return The users as DTO
     */
    @Named("toUsersDtos")
    @IterableMapping(qualifiedByName = "toUserDto")
    public abstract List<UserResponseDto> toUsersDtos(Collection<User> users);

    /**
     * Map a connected user to user entity.
     *
     * @param username             The username
     * @param firstname            The user firstname
     * @param lastname             The user lastname
     * @param email                The user email
     * @param authenticationMethod The ID provider used
     * @return The user entity
     */
    @Named("connectedUserToUserEntity")
    public abstract User connectedUserToUserEntity(String username, String firstname, String lastname, String email,
                                                   String avatarUrl, AuthenticationProvider authenticationMethod);

    /**
     * Map a user DTO into a user as entity.
     *
     * @param userRequestDto       The userRequestDto to map
     * @param authenticationMethod The authentication method of the user
     * @return The user entity
     */
    @Named("toUserEntity")
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "authenticationMethod", source = "authenticationMethod")
    @Mapping(target = "password", expression = "java( passwordEncoder.encode(userRequestDto.getPassword()) )")
    public abstract User toUserEntity(UserRequestDto userRequestDto, AuthenticationProvider authenticationMethod);
}
