package com.michelin.suricate.services.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.entities.PersonalAccessToken;
import com.michelin.suricate.model.entities.Role;
import com.michelin.suricate.model.entities.User;
import com.michelin.suricate.repositories.PersonalAccessTokenRepository;
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

        assertThat(actual)
            .hasSize(1)
            .contains(personalAccessToken);

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

        assertThat(actual)
            .isPresent()
            .contains(personalAccessToken);

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

        assertThat(actual)
            .isPresent()
            .contains(personalAccessToken);

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

        assertThat(actual.getName()).isEqualTo("token");
        assertThat(actual.getChecksum()).isEqualTo(1L);
        assertThat(actual.getUser()).isEqualTo(localUser.getUser());

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
