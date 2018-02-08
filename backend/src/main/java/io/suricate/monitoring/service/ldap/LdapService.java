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

package io.suricate.monitoring.service.ldap;

import io.suricate.monitoring.config.ldap.ConnectedUserSimpleMapper;
import io.suricate.monitoring.config.security.ConnectedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.WhitespaceWildcardsFilter;
import org.springframework.stereotype.Service;

import javax.naming.directory.SearchControls;
import java.util.List;

@Service
public class LdapService {

    /**
     * Class logger
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(LdapService.class);

    /**
     * Ldap user limit
     */
    private static final int COUNT_LIMIT = 10;
    /**
     * Ldap time limit
     */
    private static final int TIME_LIMIT = 2300;

    @Autowired
    private LdapTemplate ldapTemplate;

    @Autowired
    private ConnectedUserSimpleMapper connectedUserSimpleMapper;

    /**
     * Method used to get the user data from it's user name
     * @param userName the user name
     * @return all information from the ldap for the specified user
     */
    @Cacheable("ldap-user-detail")
    public ConnectedUser getUserDetails(String userName) {
        String search = userName.replaceAll("[^a-zA-Z0-9]*","");
        AndFilter filter = new AndFilter();
        filter.and(new EqualsFilter("uid", search)).and(new EqualsFilter("objectclass", "inetOrgPerson"));
        String stringFilter = filter.encode();
        LOGGER.debug("LDAP Filter is {}", stringFilter);
        List<ConnectedUser> users = null;
        try {
            users = ldapTemplate.search("", stringFilter, connectedUserSimpleMapper);
        } catch (Exception e){
            LOGGER.debug(e.getMessage(), e);
        }
        if (users != null && !users.isEmpty()) {
            return users.get(0);
        }
        return null;
    }

    /**
     * Method used to search user with it's userName
     * @param userName the user name to search
     * @return the list of connected users
     */
    @Cacheable("ldap-user-search")
    public List<ConnectedUser> searchUser(String userName) {
        String search = userName.replaceAll("[^a-zA-Z0-9]*","");
        List<ConnectedUser> ret = null;
        SearchControls searchControls = new SearchControls();
        searchControls.setCountLimit(COUNT_LIMIT);
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        searchControls.setTimeLimit(TIME_LIMIT);
        AndFilter filter = new AndFilter();
        filter.and(new WhitespaceWildcardsFilter("uid", search)).and(new EqualsFilter("objectclass", "inetOrgPerson"));
        LOGGER.debug("LDAP Filter is {}", filter.encode());
        try {
            ldapTemplate.setIgnoreSizeLimitExceededException(true);
            ret = ldapTemplate.search("", filter.encode(), searchControls, connectedUserSimpleMapper);
        } catch (Exception e){
            LOGGER.debug(e.getMessage(), e);
        }
        return ret;
    }
}
