package com.michelin.suricate.service.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entity.PersonalAccessToken;
import com.michelin.suricate.model.entity.Role;
import com.michelin.suricate.model.entity.User;
import com.michelin.suricate.repository.PersonalAccessTokenRepository;
import com.michelin.suricate.security.LocalUser;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonalAccessTokenServiceTest {
    @Mock
    private PersonalAccessTokenRepository personalAccessTokenRepository;

    @InjectMocks
    private PersonalAccessTokenService personalAccessTokenService;

    @Test
    void shouldFindAllByUser() {
        PersonalAccessToken personalAccessToken = new PersonalAccessToken();
        personalAccessToken.setId(1L);

        User user = new User();
        user.setId(1L);

        when(personalAccessTokenRepository.findAllByUser(any()))
            .thenReturn(Collections.singletonList(personalAccessToken));

        List<PersonalAccessToken> actual = personalAccessTokenService.findAllByUser(user);

        assertEquals(1, actual.size());
        assertTrue(actual.contains(personalAccessToken));

        verify(personalAccessTokenRepository)
            .findAllByUser(user);
    }

    @Test
    void shouldFindByNameAndUser() {
        PersonalAccessToken personalAccessToken = new PersonalAccessToken();
        personalAccessToken.setId(1L);

        User user = new User();
        user.setId(1L);

        when(personalAccessTokenRepository.findByNameAndUser(any(), any()))
            .thenReturn(Optional.of(personalAccessToken));

        Optional<PersonalAccessToken> actual = personalAccessTokenService.findByNameAndUser("name", user);

        assertTrue(actual.isPresent());
        assertEquals(personalAccessToken, actual.get());

        verify(personalAccessTokenRepository)
            .findByNameAndUser("name", user);
    }

    @Test
    void shouldFindByChecksum() {
        PersonalAccessToken personalAccessToken = new PersonalAccessToken();
        personalAccessToken.setId(1L);

        when(personalAccessTokenRepository.findByChecksum(any()))
            .thenReturn(Optional.of(personalAccessToken));

        Optional<PersonalAccessToken> actual = personalAccessTokenService.findByChecksum(1L);

        assertTrue(actual.isPresent());
        assertEquals(personalAccessToken, actual.get());

        verify(personalAccessTokenRepository)
            .findByChecksum(1L);
    }

    @Test
    void shouldCreate() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("password");
        user.setRoles(Collections.singleton(role));

        LocalUser localUser = new LocalUser(user, Collections.emptyMap());

        when(personalAccessTokenRepository.save(any()))
            .thenAnswer(answer -> answer.getArgument(0));

        PersonalAccessToken actual = personalAccessTokenService.create("token", 1L, localUser);

        assertEquals("token", actual.getName());
        assertEquals(1L, actual.getChecksum());
        assertEquals(localUser.getUser(), actual.getUser());

        verify(personalAccessTokenRepository)
            .save(argThat(createdPersonalAccessToken -> createdPersonalAccessToken.getName().equals("token")
                && createdPersonalAccessToken.getChecksum().equals(1L)
                && createdPersonalAccessToken.getUser().equals(localUser.getUser())));
    }

    @Test
    void shouldDeleteById() {
        personalAccessTokenService.deleteById(1L);

        verify(personalAccessTokenRepository)
            .deleteById(1L);
    }
}
