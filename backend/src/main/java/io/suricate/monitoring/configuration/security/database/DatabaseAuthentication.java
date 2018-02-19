package io.suricate.monitoring.configuration.security.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Database Authentification configurations
 */
@Configuration
@ConditionalOnProperty(name = "application.authentication.provider", havingValue = "database")
public class DatabaseAuthentication {

    /**
     * Configure the Authentication manager for the database
     *
     * @param auth The auth builder injected by Spring
     */
    @Autowired
    public void configureDatabase(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .jdbcAuthentication()
                .passwordEncoder(new BCryptPasswordEncoder())
                .authoritiesByUsernameQuery("select u.username, r.name from user u, user_role ur, role r where ur.user_id = u.id and ur.role_id = r.id and u.username = ?")
                .usersByUsernameQuery("select username, password as password,1 FROM user where username = ?");
    }
}
