package com.michelin.suricate.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.michelin.suricate.model.dto.api.token.JwtAuthenticationResponseDto;
import com.michelin.suricate.model.dto.api.user.SignInRequestDto;
import com.michelin.suricate.service.token.JwtHelperService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {
    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtHelperService jwtHelperService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    void shouldSignIn() {
        SignInRequestDto signInRequestDto = new SignInRequestDto();
        signInRequestDto.setUsername("username");
        signInRequestDto.setPassword("password");

        when(authenticationManager.authenticate(any()))
            .thenReturn(authentication);
        when(jwtHelperService.createToken(any()))
            .thenReturn("token");

        ResponseEntity<JwtAuthenticationResponseDto> actual = authenticationController.signIn(signInRequestDto);

        assertEquals(MediaType.APPLICATION_JSON, actual.getHeaders().getContentType());
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertNotNull(actual.getBody());
        assertEquals("token", actual.getBody().getAccessToken());

        verify(authenticationManager)
            .authenticate(Mockito.<UsernamePasswordAuthenticationToken>argThat(auth ->
                auth.getPrincipal().equals("username")
                    && auth.getCredentials().equals("password")));
        verify(jwtHelperService)
            .createToken(authentication);
    }
}
