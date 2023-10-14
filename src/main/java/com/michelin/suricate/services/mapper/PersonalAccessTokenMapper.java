package com.michelin.suricate.services.mapper;

import com.michelin.suricate.model.dto.api.token.PersonalAccessTokenResponseDto;
import com.michelin.suricate.model.entities.PersonalAccessToken;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

/**
 * Personal access token mapper.
 */
@Component
@Mapper(componentModel = "spring")
public abstract class PersonalAccessTokenMapper {
    /**
     * Map a token into a DTO.
     *
     * @param personalAccessToken The token to map
     * @return The token as DTO
     */
    @Named("toPersonalAccessTokenDto")
    public abstract PersonalAccessTokenResponseDto toPersonalAccessTokenDto(PersonalAccessToken personalAccessToken,
                                                                            String value);

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
