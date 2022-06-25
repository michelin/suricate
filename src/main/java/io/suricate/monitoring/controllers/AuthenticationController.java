package io.suricate.monitoring.controllers;

import io.suricate.monitoring.model.dto.api.token.JwtAuthenticationResponseDto;
import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.api.user.SignInRequestDto;
import io.suricate.monitoring.model.dto.api.user.UserResponseDto;
import io.suricate.monitoring.services.token.JwtHelperService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication controller
 */
@RestController
@RequestMapping("/api")
@Api(value = "Authentication Controller", tags = {"Authentication"})
public class AuthenticationController {
    /**
     * The authentication manager
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * The token provider
     */
    @Autowired
    private JwtHelperService jwtHelperService;

    /**
     * Sign in a user
     * @param signInRequestDto The sign in request
     * @return An authentication response
     */
    @ApiOperation(value = "Sign in a user", response = UserResponseDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok", response = UserResponseDto.class),
            @ApiResponse(code = 400, message = "Bad request", response = ApiErrorDto.class),
    })
    @PostMapping(value = "/v1/auth/signin")
    @PreAuthorize("isAnonymous()")
    public ResponseEntity<JwtAuthenticationResponseDto> signIn(@ApiParam(name = "signInRequestDto", value = "The sign in request", required = true)
                                                               @RequestBody SignInRequestDto signInRequestDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequestDto.getUsername(), signInRequestDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtHelperService.createToken(authentication);
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new JwtAuthenticationResponseDto(token));
    }
}
