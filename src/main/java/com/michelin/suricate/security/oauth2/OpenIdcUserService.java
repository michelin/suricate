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

import static org.apache.commons.lang3.StringUtils.SPACE;

import com.michelin.suricate.model.entity.User;
import com.michelin.suricate.model.enumeration.AuthenticationProvider;
import com.michelin.suricate.property.ApplicationProperties;
import com.michelin.suricate.security.LocalUser;
import com.michelin.suricate.service.api.UserService;
import com.michelin.suricate.util.exception.Oauth2AuthenticationProcessingException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

/** OIDC user service. */
@Slf4j
@Service
public class OpenIdcUserService extends OidcUserService {
    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationProperties applicationProperties;

    /**
     * Load a user after he has been successfully authenticated with OIDC ID providers.
     *
     * @param userRequest The user information
     * @return An OIDC user
     * @throws OAuth2AuthenticationException Any OAuth2 authentication exception
     */
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        try {
            Optional<AuthenticationProvider> authenticationMethod = Arrays.stream(AuthenticationProvider.values())
                    .filter(authMethod -> userRequest
                            .getClientRegistration()
                            .getRegistrationId()
                            .equalsIgnoreCase(authMethod.name()))
                    .findAny();

            if (authenticationMethod.isEmpty()) {
                throw new Oauth2AuthenticationProcessingException(String.format(
                        "ID provider %s is not recognized",
                        userRequest.getClientRegistration().getRegistrationId()));
            }

            String username = oidcUser.getAttribute("nickname");
            if (StringUtils.isBlank(username)) {
                throw new Oauth2AuthenticationProcessingException(String.format(
                        "Username not found from %s",
                        userRequest.getClientRegistration().getRegistrationId()));
            }

            String email = oidcUser.getAttribute("email");
            if (StringUtils.isBlank(email)) {
                throw new Oauth2AuthenticationProcessingException(String.format(
                        "Email not found from %s",
                        StringUtils.capitalize(
                                userRequest.getClientRegistration().getRegistrationId())));
            }

            String firstName = null;
            String lastName = null;
            String name = oidcUser.getAttribute("name");
            if (StringUtils.isNotBlank(name)) {
                List<String> splitName = new LinkedList<>(Arrays.asList(name.split(SPACE)));
                if (applicationProperties
                                .getAuthentication()
                                .getSocialProvidersConfig()
                                .containsKey(userRequest
                                        .getClientRegistration()
                                        .getRegistrationId()
                                        .toLowerCase())
                        && applicationProperties
                                .getAuthentication()
                                .getSocialProvidersConfig()
                                .get(userRequest
                                        .getClientRegistration()
                                        .getRegistrationId()
                                        .toLowerCase())
                                .isNameCaseParse()) {
                    firstName = splitName.stream()
                            .filter(word -> !word.equals(word.toUpperCase()))
                            .collect(Collectors.joining(SPACE));
                    lastName = splitName.stream()
                            .filter(word -> word.equals(word.toUpperCase()))
                            .collect(Collectors.joining(SPACE));
                } else {
                    firstName = splitName.getFirst();
                    lastName = String.join(SPACE, splitName.subList(1, splitName.size()));
                }
            }

            String avatarUrl = null;
            if (oidcUser.getAttribute("avatar_url") != null) {
                avatarUrl = oidcUser.getAttribute("avatar_url");
            } else if (oidcUser.getAttribute("picture") != null) {
                avatarUrl = oidcUser.getAttribute("picture");
            }

            User user = userService.registerUser(
                    username, firstName, lastName, email, avatarUrl, authenticationMethod.get());

            log.debug(
                    "Authenticated user <{}> with {}",
                    username,
                    userRequest.getClientRegistration().getRegistrationId());

            return new LocalUser(user, oidcUser.getAttributes(), oidcUser.getIdToken(), oidcUser.getUserInfo());
        } catch (Exception e) {
            log.error(
                    "An error occurred authenticating user <{}> with {} in OIDC mode",
                    oidcUser.getName(),
                    userRequest.getClientRegistration().getRegistrationId(),
                    e);
            throw new Oauth2AuthenticationProcessingException(e.getMessage(), e.getCause());
        }
    }
}
