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
import com.michelin.suricate.util.web.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/** Handle OAuth2 authentication failure. */
@Slf4j
@Component
public class Oauth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    /** The authentication request repository Store the authentication request in an HTTP cookie on the IDP response. */
    @Autowired
    private HttpCookieOauth2AuthorizationRequestRepository authorizationRequestRepository;

    /** The application properties. */
    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Trigger after OAuth2 authentication has failed.
     *
     * @param request The request which is the response of the IDP
     * @param response The response to send to the host that authenticated successfully
     * @param exception The authentication exception
     * @throws IOException Any IO Exception
     */
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException {
        Optional<String> redirectUri =
                CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);

        if (redirectUri.isEmpty()
                && applicationProperties.getAuthentication().getOauth2().isUseReferer()) {
            redirectUri = Optional.ofNullable(request.getHeader(HttpHeaders.REFERER));
            redirectUri.ifPresent(
                    redirect -> log.debug("Using url {} from Referer header", request.getHeader(HttpHeaders.REFERER)));
        }

        String targetUrl = redirectUri.orElse(
                applicationProperties.getAuthentication().getOauth2().getDefaultTargetUrl());
        if (StringUtils.isBlank(targetUrl)) {
            targetUrl = "/";
        }

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", exception.getLocalizedMessage())
                .build()
                .toUriString();

        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
