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

package io.suricate.monitoring.controllers.backoffice.user;


import io.suricate.monitoring.controllers.backoffice.AbstractManagedBean;
import io.suricate.monitoring.model.user.User;
import io.suricate.monitoring.repository.UserRepository;
import io.suricate.monitoring.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@ViewScoped
@PreAuthorize("hasRole('ROLE_USER')")
@Named(value = "ProfileManagedBean")
public class ProfileManagedBean extends AbstractManagedBean {

    /**
     * Selected user
     */
    private User user;

    @Autowired
    private transient UserRepository userRepository;

    /**
     * Extract parameters
     */
    @PostConstruct
    public void init(){
        user = userRepository.findByUsername(SecurityUtils.getConnectedUser().getUsername());
    }

    public User getUser() {
        return user;
    }

}

