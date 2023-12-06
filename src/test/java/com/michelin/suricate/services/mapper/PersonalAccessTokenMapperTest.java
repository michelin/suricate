package com.michelin.suricate.services.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.michelin.suricate.model.dto.api.token.PersonalAccessTokenResponseDto;
import com.michelin.suricate.model.entities.PersonalAccessToken;
import com.michelin.suricate.model.entities.User;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonalAccessTokenMapperTest {
    @InjectMocks
    private PersonalAccessTokenMapperImpl personalAccessTokenMapper;

    @Test
    void shouldToPersonalAccessTokenDto() {
        PersonalAccessToken personalAccessToken = new PersonalAccessToken();
        personalAccessToken.setId(1L);
        personalAccessToken.setName("name");
        personalAccessToken.setChecksum(10L);
        personalAccessToken.setUser(new User());
        personalAccessToken.setCreatedDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));

        PersonalAccessTokenResponseDto actual =
            personalAccessTokenMapper.toPersonalAccessTokenDto(personalAccessToken, "value");

        assertThat(actual.getValue()).isEqualTo("value");
        assertThat(actual.getName()).isEqualTo("name");
        assertThat(actual.getCreatedDate()).isEqualTo(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
    }

    @Test
    void shouldToPersonalAccessTokenNoValueDto() {
        PersonalAccessToken personalAccessToken = new PersonalAccessToken();
        personalAccessToken.setId(1L);
        personalAccessToken.setName("name");
        personalAccessToken.setChecksum(10L);
        personalAccessToken.setUser(new User());
        personalAccessToken.setCreatedDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));

        PersonalAccessTokenResponseDto actual =
            personalAccessTokenMapper.toPersonalAccessTokenNoValueDto(personalAccessToken);

        assertThat(actual.getValue()).isNull();
        assertThat(actual.getName()).isEqualTo("name");
        assertThat(actual.getCreatedDate()).isEqualTo(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
    }

    @Test
    void shouldToPersonalAccessTokensDtos() {
        PersonalAccessToken personalAccessToken = new PersonalAccessToken();
        personalAccessToken.setId(1L);
        personalAccessToken.setName("name");
        personalAccessToken.setChecksum(10L);
        personalAccessToken.setUser(new User());
        personalAccessToken.setCreatedDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));

        List<PersonalAccessTokenResponseDto> actual =
            personalAccessTokenMapper.toPersonalAccessTokensDtos(Collections.singletonList(personalAccessToken));

        assertThat(actual.get(0).getValue()).isNull();
        assertThat(actual.get(0).getName()).isEqualTo("name");
        assertThat(actual.get(0).getCreatedDate()).isEqualTo(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
    }
}
