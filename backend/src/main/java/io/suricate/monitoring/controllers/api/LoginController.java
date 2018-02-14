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

package io.suricate.monitoring.controllers.api;

import io.suricate.monitoring.config.security.token.TokenService;
import io.suricate.monitoring.controllers.api.error.exception.ApiException;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.suricate.monitoring.model.dto.token.TokenResponse;
import io.suricate.monitoring.model.enums.UserRoleEnum;
import io.suricate.monitoring.model.user.Role;
import io.suricate.monitoring.model.user.User;
import io.suricate.monitoring.repository.RoleRepository;
import io.suricate.monitoring.repository.UserRepository;
import io.suricate.monitoring.utils.ApplicationConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;

@RestController
@RequestMapping("/${api.prefix}/login")
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final TokenService tokenService;

    @Autowired
    public LoginController(UserRepository userRepository, RoleRepository roleRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.tokenService = tokenService;
    }

    /**
     * Authenticate a user
     * @return A new user token
     */
    @RequestMapping(method = RequestMethod.POST)
    public TokenResponse authenticate(Principal principal) {
        TokenResponse response = new TokenResponse();

        if (principal == null || StringUtils.isBlank(principal.getName())) {
            throw new ApiException(ApiErrorEnum.USER_NOT_FOUND);
        }

        Optional<User> userOptional = userRepository.findByUsername(principal.getName());
        User user;

        if (!userOptional.isPresent()) {
            if (userRepository.count() != 0) {
                throw new ApiException(ApiErrorEnum.USER_NOT_FOUND);
            }
            LOGGER.debug("No user found. Add {} as ADMIN", principal.getName());
            user = new User();
            user.setUsername(principal.getName());

            Optional<Role> role = roleRepository.findByName(UserRoleEnum.ROLE_ADMIN.name());

            if(role.isPresent()) {
                user.setRoles(Arrays.asList(role.get()));
            } else {
                throw new IllegalArgumentException();
            }

        } else {
            user = userOptional.get();
        }
        user.setToken(tokenService.generateToken());
        userRepository.save(user);
        response.token = user.getToken();

        return response;
    }


}
