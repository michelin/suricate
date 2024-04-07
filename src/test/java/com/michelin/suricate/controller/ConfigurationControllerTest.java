package com.michelin.suricate.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.enumeration.AuthenticationProvider;
import com.michelin.suricate.property.ApplicationProperties;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ConfigurationControllerTest {
    @Mock
    private ApplicationProperties applicationProperties;

    @InjectMocks
    private ConfigurationController configurationController;

    @Test
    void shouldGetAuthenticationProviders() {
        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();
        authProperties.setProvider("ldap");
        authProperties.setSocialProviders(Collections.singletonList("github"));

        when(applicationProperties.getAuthentication()).thenReturn(authProperties);

        ResponseEntity<List<AuthenticationProvider>> actual = configurationController.getAuthenticationProviders();

        assertThat(actual.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody()).hasSize(2);
        assertThat(actual.getBody())
            .contains(AuthenticationProvider.LDAP)
            .contains(AuthenticationProvider.GITHUB);
    }

    @Test
    void shouldGetAuthenticationProvidersEmpty() {
        ApplicationProperties.Authentication authProperties = new ApplicationProperties.Authentication();

        when(applicationProperties.getAuthentication()).thenReturn(authProperties);

        ResponseEntity<List<AuthenticationProvider>> actual = configurationController.getAuthenticationProviders();

        assertThat(actual.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(actual.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual.getBody()).isNotNull();
        assertThat(actual.getBody()).isEmpty();
    }
}
