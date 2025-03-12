/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.michelin.suricate.service.mapper;

import com.michelin.suricate.model.dto.api.token.PersonalAccessTokenResponseDto;
import com.michelin.suricate.model.entity.PersonalAccessToken;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

/** Personal access token mapper. */
@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class PersonalAccessTokenMapper {
    /**
     * Map a token into a DTO.
     *
     * @param personalAccessToken The token to map
     * @return The token as DTO
     */
    @Named("toPersonalAccessTokenDto")
    public abstract PersonalAccessTokenResponseDto toPersonalAccessTokenDto(
            PersonalAccessToken personalAccessToken, String value);

    /**
     * Map a token into a DTO hiding the token value.
     *
     * @param personalAccessToken The token to map
     * @return The token as DTO
     */
    @Named("toPersonalAccessTokenNoValueDto")
    public abstract PersonalAccessTokenResponseDto toPersonalAccessTokenNoValueDto(
            PersonalAccessToken personalAccessToken);

    /**
     * Map a list of tokens into a list of DTOs.
     *
     * @param personalAccessTokens The list of tokens to map
     * @return The list of tokens as DTOs
     */
    @Named("toPersonalAccessTokensDtos")
    @IterableMapping(qualifiedByName = "toPersonalAccessTokenNoValueDto")
    public abstract List<PersonalAccessTokenResponseDto> toPersonalAccessTokensDtos(
            List<PersonalAccessToken> personalAccessTokens);
}
