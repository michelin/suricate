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

package io.suricate.monitoring.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.suricate.monitoring.model.dto.error.CustomError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BasicAuthenticationEntryPoint extends org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint {
    /**
     * Class LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicAuthenticationEntryPoint.class);

    /**
     * This method is called when authentication fails.
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        LOGGER.debug("Authentication error - {}", path, authException);

        // Format response
        response.setStatus(ApiErrorEnum.AUTHENTICATION_ERROR.getStatus().value());
        response.setHeader("Content-type","application/json");
        ObjectMapper obj = new ObjectMapper();
        obj.writeValue(response.getOutputStream(), new CustomError(ApiErrorEnum.AUTHENTICATION_ERROR));
    }

}
