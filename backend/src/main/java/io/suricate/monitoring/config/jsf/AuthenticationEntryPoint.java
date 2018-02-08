/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.suricate.monitoring.config.jsf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    /**
     * Class logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationEntryPoint.class);

    /**
     * JSF redirect strategy
     */
    private final JsfRedirectStrategy jsfRedirectStrategy = new JsfRedirectStrategy();


    /**
     * Authentication Entry point used to define login path
     * @param loginFormUrl the login path
     */
    public AuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        LOGGER.debug("request: {}, exception: {}", request.getRequestURI(), authException);
        String redirectUrl = this.buildRedirectUrlToLoginPage(request, response, authException);
        LOGGER.debug("Redirect URL: {}", redirectUrl);
        jsfRedirectStrategy.sendRedirect(request, response, redirectUrl);
    }
}
