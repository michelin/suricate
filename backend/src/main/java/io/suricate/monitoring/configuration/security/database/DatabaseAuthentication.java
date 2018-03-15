package io.suricate.monitoring.configuration.security.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Database Authentification configurations
 */
@Configuration
@ConditionalOnProperty(name = "application.authentication.provider", havingValue = "database")
public class DatabaseAuthentication {

    private final UserDetailsDatabaseService userDetailsDatabaseService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DatabaseAuthentication(UserDetailsDatabaseService userDetailsDatabaseService, final PasswordEncoder passwordEncoder) {
        this.userDetailsDatabaseService = userDetailsDatabaseService;
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void configureDatabase(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userDetailsDatabaseService)
            .passwordEncoder(passwordEncoder);
    }
}
