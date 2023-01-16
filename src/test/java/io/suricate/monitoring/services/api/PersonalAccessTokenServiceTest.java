package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.entities.PersonalAccessToken;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.repositories.PersonalAccessTokenRepository;
import io.suricate.monitoring.security.LocalUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

        verify(personalAccessTokenRepository, times(1))
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

        verify(personalAccessTokenRepository, times(1))
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

        verify(personalAccessTokenRepository, times(1))
                .findByChecksum(1L);
    }

    @Test
    void shouldCreate() {
        Authentication authentication = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public LocalUser getPrincipal() {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("key", "value");

                User user = new User();
                user.setId(1L);
                user.setUsername("username");
                user.setPassword("password");

                return new LocalUser(user, attributes);
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return null;
            }
        };

        when(personalAccessTokenRepository.save(any()))
                .thenAnswer(answer -> answer.getArgument(0));

        PersonalAccessToken actual = personalAccessTokenService.create("token", 1L, authentication);

        assertThat(actual.getName()).isEqualTo("token");
        assertThat(actual.getChecksum()).isEqualTo(1L);
        assertThat(actual.getUser()).isEqualTo(((LocalUser) authentication.getPrincipal()).getUser());

        verify(personalAccessTokenRepository, times(1))
                .save(argThat(createdPersonalAccessToken -> createdPersonalAccessToken.getName().equals("token") &&
                        createdPersonalAccessToken.getChecksum().equals(1L) &&
                        createdPersonalAccessToken.getUser().equals(((LocalUser) authentication.getPrincipal()).getUser())));
    }

    @Test
    void shouldDeleteById() {
        doNothing().when(personalAccessTokenRepository)
                .deleteById(any());

        personalAccessTokenService.deleteById(1L);

        verify(personalAccessTokenRepository, times(1))
                .deleteById(1L);
    }
}
