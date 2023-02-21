package com.michelin.suricate.services.mapper;

import com.michelin.suricate.model.dto.api.token.PersonalAccessTokenResponseDto;
import com.michelin.suricate.model.entities.PersonalAccessToken;
import com.michelin.suricate.model.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PersonalAccessTokenMapperTest {
    @InjectMocks
    private PersonalAccessTokenMapperImpl personalAccessTokenMapper;

    @Test
    void shouldToPersonalAccessTokenDTO() {
        PersonalAccessToken personalAccessToken = new PersonalAccessToken();
        personalAccessToken.setId(1L);
        personalAccessToken.setName("name");
        personalAccessToken.setChecksum(10L);
        personalAccessToken.setUser(new User());
        personalAccessToken.setCreatedDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));

        PersonalAccessTokenResponseDto actual = personalAccessTokenMapper.toPersonalAccessTokenDTO(personalAccessToken, "value");

        assertThat(actual.getValue()).isEqualTo("value");
        assertThat(actual.getName()).isEqualTo("name");
        assertThat(actual.getCreatedDate()).isEqualTo(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
    }

    @Test
    void shouldToPersonalAccessTokenNoValueDTO() {
        PersonalAccessToken personalAccessToken = new PersonalAccessToken();
        personalAccessToken.setId(1L);
        personalAccessToken.setName("name");
        personalAccessToken.setChecksum(10L);
        personalAccessToken.setUser(new User());
        personalAccessToken.setCreatedDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));

        PersonalAccessTokenResponseDto actual = personalAccessTokenMapper.toPersonalAccessTokenNoValueDTO(personalAccessToken);

        assertThat(actual.getValue()).isNull();
        assertThat(actual.getName()).isEqualTo("name");
        assertThat(actual.getCreatedDate()).isEqualTo(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
    }

    @Test
    void shouldToPersonalAccessTokensDTOs() {
        PersonalAccessToken personalAccessToken = new PersonalAccessToken();
        personalAccessToken.setId(1L);
        personalAccessToken.setName("name");
        personalAccessToken.setChecksum(10L);
        personalAccessToken.setUser(new User());
        personalAccessToken.setCreatedDate(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));

        List<PersonalAccessTokenResponseDto> actual = personalAccessTokenMapper.toPersonalAccessTokensDTOs(Collections.singletonList(personalAccessToken));

        assertThat(actual.get(0).getValue()).isNull();
        assertThat(actual.get(0).getName()).isEqualTo("name");
        assertThat(actual.get(0).getCreatedDate()).isEqualTo(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")));
    }
}
