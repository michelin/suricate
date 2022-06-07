package io.suricate.monitoring.services.mapper;

import io.suricate.monitoring.model.dto.api.project.ProjectResponseDto;
import io.suricate.monitoring.model.dto.api.token.TokenResponseDto;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.Token;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(
        componentModel = "spring"
)
public abstract class TokenMapper {
    /**
     * Map a token into a DTO
     * @param token The token to map
     * @return The token as DTO
     */
    @Named("toTokenDTO")
    public abstract TokenResponseDto toTokenDTO(Token token);

    /**
     * Map a token into a DTO hiding the token value
     * @param token The token to map
     * @return The token as DTO
     */
    @Named("toTokenHiddenValueDTO")
    @Mapping(target = "value", ignore = true)
    public abstract TokenResponseDto toTokenHiddenValueDTO(Token token);

    /**
     * Map a list of tokens into a list of DTOs
     * @param tokens The list of tokens to map
     * @return The list of tokens as DTOs
     */
    @Named("toTokensDTOs")
    @IterableMapping(qualifiedByName = "toTokenHiddenValueDTO")
    public abstract List<TokenResponseDto> toTokensDTOs(List<Token> tokens);
}
