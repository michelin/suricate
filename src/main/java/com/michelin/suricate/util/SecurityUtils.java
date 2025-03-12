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
package com.michelin.suricate.util;

import com.michelin.suricate.model.enumeration.UserRoleEnum;
import com.michelin.suricate.security.LocalUser;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/** Security utils. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityUtils {
    /**
     * Method used to isValid if the connected user is admin.
     *
     * @return true if the connected user is admin, false otherwise
     */
    public static boolean isAdmin(final LocalUser connectedUser) {
        return hasRole(connectedUser, UserRoleEnum.ROLE_ADMIN.name());
    }

    /**
     * Method used to isValid if the connected user as all role in list.
     *
     * @param roles list of roles
     * @return true if the connected user have all roles, false otherwise
     */
    public static boolean hasRole(LocalUser connectedUser, String... roles) {
        boolean ret = false;

        if (connectedUser != null) {
            List<GrantedAuthority> list = new ArrayList<>();
            for (String role : roles) {
                list.add(new SimpleGrantedAuthority(role));
            }
            ret = connectedUser.getAuthorities().containsAll(list);
        }

        return ret;
    }
}
