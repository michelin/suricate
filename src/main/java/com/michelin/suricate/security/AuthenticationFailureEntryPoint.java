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
package com.michelin.suricate.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.michelin.suricate.model.dto.api.error.ApiErrorDto;
import com.michelin.suricate.model.enumeration.ApiErrorEnum;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/** Handle authentication and access denied exceptions. */
@Slf4j
@Component
public class AuthenticationFailureEntryPoint implements AuthenticationEntryPoint, AccessDeniedHandler {
    /**
     * Process the authentication/access exceptions.
     *
     * @param httpServletRequest The request
     * @param httpServletResponse The response
     * @param e The exception
     * @throws IOException Any IO exception
     */
    private static void resolveException(
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, RuntimeException e)
            throws IOException {
        String path = httpServletRequest
                .getRequestURI()
                .substring(httpServletRequest.getContextPath().length());
        log.debug("Authentication error - {}", path, e);

        httpServletResponse.setStatus(
                ApiErrorEnum.AUTHENTICATION_ERROR.getStatus().value());
        httpServletResponse.setHeader("Content-type", "application/json");

        ObjectMapper obj = new ObjectMapper();
        obj.writeValue(httpServletResponse.getOutputStream(), new ApiErrorDto(ApiErrorEnum.AUTHENTICATION_ERROR));
    }

    /**
     * Handle authentication exception.
     *
     * @param httpServletRequest The request
     * @param httpServletResponse The response
     * @param e The exception
     * @throws IOException Any IO exception
     */
    @Override
    public void commence(
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e)
            throws IOException {
        resolveException(httpServletRequest, httpServletResponse, e);
    }

    /**
     * Handle access denied exception.
     *
     * @param httpServletRequest The request
     * @param httpServletResponse The response
     * @param e The exception
     * @throws IOException Any IO exception
     * @throws ServletException Any servlet exception
     */
    @Override
    public void handle(
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e)
            throws IOException, ServletException {
        resolveException(httpServletRequest, httpServletResponse, e);
    }
}
