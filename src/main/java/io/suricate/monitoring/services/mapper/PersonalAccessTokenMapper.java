package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.token.PersonalAccessTokenResponseDto;
import io.suricate.monitoring.model.entities.PersonalAccessToken;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public abstract class PersonalAccessTokenMapper {
    /**
     * Map a token into a DTO
     * @param personalAccessToken The token to map
     * @return The token as DTO
     */
    @Named("toPersonalAccessTokenDTO")
    public abstract PersonalAccessTokenResponseDto toPersonalAccessTokenDTO(PersonalAccessToken personalAccessToken, String value);

    /**
     * Map a token into a DTO hiding the token value
     * @param personalAccessToken The token to map
     * @return The token as DTO
     */
    @Named("toPersonalAccessTokenNoValueDTO")
    public abstract PersonalAccessTokenResponseDto toPersonalAccessTokenNoValueDTO(PersonalAccessToken personalAccessToken);

    /**
     * Map a list of tokens into a list of DTOs
     * @param personalAccessTokens The list of tokens to map
     * @return The list of tokens as DTOs
     */
    @Named("toPersonalAccessTokensDTOs")
    @IterableMapping(qualifiedByName = "toPersonalAccessTokenNoValueDTO")
    public abstract List<PersonalAccessTokenResponseDto> toPersonalAccessTokensDTOs(List<PersonalAccessToken> personalAccessTokens);
}
