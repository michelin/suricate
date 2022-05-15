package io.suricate.monitoring.configuration.security.database;

import io.suricate.monitoring.configuration.security.common.ConnectedUser;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.model.enums.AuthenticationMethod;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class DatabaseConnectedUser extends ConnectedUser {
    /**
     * Constructor
     * @param user The user to connect
     * @param authorities The list of authorities for this user
     */
    public DatabaseConnectedUser(User user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getUsername(), user.getPassword(), true, true, true, true, authorities);

        this.id = user.getId();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.email = user.getEmail();
        this.authenticationMethod = AuthenticationMethod.DATABASE;
    }
}
