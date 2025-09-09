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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClientRoutingFilterTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private RequestDispatcher requestDispatcher;

    private final ClientRoutingFilter filter = new ClientRoutingFilter();

    @ParameterizedTest
    @ValueSource(strings = {"/", "/dashboards", "/dashboard/123"})
    void shouldForwardToIndexHtml(String uri) throws ServletException, IOException {
        when(request.getContextPath()).thenReturn("");
        when(request.getRequestURI()).thenReturn(uri);
        when(request.getRequestDispatcher("/index.html")).thenReturn(requestDispatcher);

        filter.doFilterInternal(request, response, filterChain);

        verify(requestDispatcher).forward(request, response);
        verify(filterChain, never()).doFilter(request, response);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "/index.html",
                "/main.js",
                "/styles.css",
                "/images/logo.png",
                "/favicon.ico",
                "/api/dashboards",
                "/ws",
                "/ws/some-endpoint",
                "/actuator/health",
                "/v3/api-docs",
                "/h2-console"
            })
    void shouldContinueFilterChain(String uri) throws ServletException, IOException {
        when(request.getContextPath()).thenReturn("");
        when(request.getRequestURI()).thenReturn(uri);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }
}
