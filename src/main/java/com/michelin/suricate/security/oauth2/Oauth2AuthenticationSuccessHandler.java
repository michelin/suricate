/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.michelin.suricate.security.oauth2;

import static com.michelin.suricate.security.oauth2.HttpCookieOauth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

import com.michelin.suricate.property.ApplicationProperties;
import com.michelin.suricate.service.token.JwtHelperService;
import com.michelin.suricate.util.web.CookieUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Handle OAuth2 authentication success.
 */
@Slf4j
@Component
public class Oauth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    /**
     * The authentication request repository
     * Store the authentication request in an HTTP cookie on the IDP response.
     */
    @Autowired
    private HttpCookieOauth2AuthorizationRequestRepository authorizationRequestRepository;

    @Getter
    @Autowired(required = false)
    private OAuth2AuthorizedClientRepository authorizedClientRepository;

    @Autowired
    private JwtHelperService tokenProvider;

    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Trigger after OAuth2 authentication has been successful.
     *
     * @param request        The request which is the response of the IDP
     * @param response       The response to send to the host that authenticated successfully
     * @param authentication The authentication data
     * @throws IOException      Any IO Exception
     * @throws ServletException Any servlet exception
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to {}", targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * Determine the host to redirect after being successfully authenticated.
     * First, try to get the host from the HTTP cookie attached on the IDP response.
     * If empty, try to get the host from the referer.
     * If still empty, get a default host.
     * Build a JWT token and add it as a parameter to the response
     *
     * @param request        The request which is the response of the IDP
     * @param response       The response to send to the host that authenticated successfully
     * @param authentication The authentication data
     * @return The host to redirect
     */
    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        Optional<String> redirectUri =
            CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);

        if (redirectUri.isEmpty() && applicationProperties.getAuthentication().getOauth2().isUseReferer()) {
            redirectUri = Optional.ofNullable(request.getHeader(HttpHeaders.REFERER));
            redirectUri.ifPresent(
                redirect -> log.debug("Using url {} from Referer header", request.getHeader(HttpHeaders.REFERER)));
        }

        String targetUrl =
            redirectUri.orElse(applicationProperties.getAuthentication().getOauth2().getDefaultTargetUrl());
        if (StringUtils.isBlank(targetUrl)) {
            targetUrl = "/";
        }

        OAuth2AuthenticationToken auth = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient authorizedClient =
            authorizedClientRepository.loadAuthorizedClient(auth.getAuthorizedClientRegistrationId(),
                authentication, request);

        log.debug("Access token generated by {}. Self-generated JWT token will be used instead.",
            authorizedClient.getClientRegistration().getRegistrationId());

        String token = tokenProvider.createToken(authentication);

        return UriComponentsBuilder.fromUriString(targetUrl)
            .queryParam("token", token)
            .build().toUriString();
    }

    /**
     * Remove authorization cookies from the IDP response before transiting it to frontend.
     *
     * @param request  The request, which is the response of the IDP, that contains the cookies to remove
     * @param response The response of the request that won't contain the cookies
     */
    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
