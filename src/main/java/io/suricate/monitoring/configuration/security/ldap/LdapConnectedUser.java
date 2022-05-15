package io.suricate.monitoring.configuration.security.ldap;

import io.suricate.monitoring.configuration.security.common.ConnectedUser;
import io.suricate.monitoring.model.enums.AuthenticationMethod;
import io.suricate.monitoring.properties.ApplicationProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class LdapConnectedUser extends ConnectedUser {
    /**
     * Constructor
     * @param username The username
     * @param userData The LDAP user data
     * @param ldapProperties The LDAP properties
     */
    public LdapConnectedUser(String username, DirContextOperations userData, ApplicationProperties.Ldap ldapProperties) {
        super(username, StringUtils.EMPTY, true, true, true, true, Collections.emptyList());

        this.firstname = userData.getStringAttribute(ldapProperties.firstNameAttributName);
        this.lastname = userData.getStringAttribute(ldapProperties.lastNameAttributName);
        this.email = userData.getStringAttribute(ldapProperties.mailAttributName);
        this.authenticationMethod = AuthenticationMethod.LDAP;
    }

    /**
     * Constructor
     * @param username The username
     * @param userData The LDAP user data
     * @param ldapProperties The LDAP properties
     */
    public LdapConnectedUser(String username, DirContextOperations userData, Collection<? extends GrantedAuthority> authorities, Long id, ApplicationProperties.Ldap ldapProperties) {
        super(username, "", true, true, true, true, authorities);

        this.id = id;
        this.firstname = userData.getStringAttribute(ldapProperties.firstNameAttributName);
        this.lastname = userData.getStringAttribute(ldapProperties.lastNameAttributName);
        this.email = userData.getStringAttribute(ldapProperties.mailAttributName);
        this.authenticationMethod = AuthenticationMethod.LDAP;
    }
}
