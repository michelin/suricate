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

package com.michelin.suricate.security.filter;

import com.michelin.suricate.model.entity.PersonalAccessToken;
import com.michelin.suricate.model.enumeration.ApiErrorEnum;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.service.api.PersonalAccessTokenService;
import com.michelin.suricate.service.token.PersonalAccessTokenHelperService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter to handle personal access token authentication.
 */
@Slf4j
public class PersonalAccessTokenFilter extends OncePerRequestFilter {
    @Autowired
    private PersonalAccessTokenHelperService patHelperService;

    @Autowired
    private PersonalAccessTokenService patService;

    /**
     * When a request arrives to the Back-End, check if it contains a personal access token.
     * If it does, validate it and set authentication to Spring context
     *
     * @param request     The incoming request
     * @param response    The response
     * @param filterChain The filter chain
     * @throws IOException Any IO exception
     */
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
        throws IOException {
        try {
            final String token = getPersonalAccessTokenFromRequest(request);

            if (StringUtils.hasText(token) && patHelperService.validateToken(token)) {
                Optional<PersonalAccessToken> patOptional =
                    patService.findByChecksum(patHelperService.computePersonAccessTokenChecksum(token));
                if (patOptional.isPresent()) {
                    LocalUser localUser = new LocalUser(patOptional.get().getUser(), Collections.emptyMap());
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(localUser, null, localUser.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Could not set user authentication in security context", e);
            response.sendError(HttpStatus.UNAUTHORIZED.value(), ApiErrorEnum.AUTHENTICATION_ERROR.getMessage());
        }
    }

    /**
     * Extract the token from a given request, if exists.
     *
     * @param request The request
     * @return The token
     */
    private String getPersonalAccessTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Token ")) {
            return bearerToken.substring(6);
        }

        return null;
    }
}
