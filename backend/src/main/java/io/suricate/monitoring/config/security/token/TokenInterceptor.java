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

package io.suricate.monitoring.config.security.token;

import io.suricate.monitoring.config.ApplicationProperties;
import io.suricate.monitoring.config.security.ConnectedUser;
import io.suricate.monitoring.controllers.api.error.exception.ApiException;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.suricate.monitoring.model.user.Role;
import io.suricate.monitoring.model.user.User;
import io.suricate.monitoring.repository.RoleRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Component(value = "TokenInterceptor")
public class TokenInterceptor implements HandlerInterceptor {

    public static final String BEARER = "Bearer ";

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        final String authHeader = httpServletRequest.getHeader("Authorization");
        if (authHeader == null || !StringUtils.startsWithIgnoreCase(authHeader,BEARER)) {
            throw new ApiException(ApiErrorEnum.TOKEN_MISSING);
        }

        final String token = authHeader.substring(BEARER.length()).trim(); // The part after "Bearer "

        //Check the token
        User user = tokenService.extractToken(token);
        if (user == null){
            throw new ApiException(ApiErrorEnum.TOKEN_INVALID);
        }

        // Add roles
        List<Role> roles = roleRepository.findByUsers_Id(user.getId());
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role: roles){
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        // Add role to spring security
        ConnectedUser connectedUser = new ConnectedUser(user.getUsername(), null, authorities, user.getId(), applicationProperties.getAuthentication().getLdap());
        Authentication authentication = new UsernamePasswordAuthenticationToken(connectedUser, token, connectedUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //Do something
        httpServletRequest.setAttribute("user", user);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        // Do nothing
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        // do nothing
    }
}
