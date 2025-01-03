package com.michelin.suricate.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.michelin.suricate.model.dto.api.token.PersonalAccessTokenResponseDto;
import com.michelin.suricate.model.entity.PersonalAccessToken;
import com.michelin.suricate.model.entity.User;
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

        assertEquals("value", actual.getValue());
        assertEquals("name", actual.getName());
        assertEquals(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")), actual.getCreatedDate());
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

        assertNull(actual.getValue());
        assertEquals("name", actual.getName());
        assertEquals(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")), actual.getCreatedDate());
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

        assertNull(actual.getFirst().getValue());
        assertEquals("name", actual.getFirst().getName());
        assertEquals(Date.from(Instant.parse("2000-01-01T01:00:00.00Z")), actual.getFirst().getCreatedDate());
    }
}
