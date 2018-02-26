package io.suricate.monitoring.configuration.security.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

/**
 * Database Authentification configurations
 */
@Configuration
@ConditionalOnProperty(name = "application.authentication.provider", havingValue = "database")
public class DatabaseAuthentication {

    private final UserDetailsDatabaseService userDetailsDatabaseService;

    @Autowired
    public DatabaseAuthentication(UserDetailsDatabaseService userDetailsDatabaseService) {
        this.userDetailsDatabaseService = userDetailsDatabaseService;
    }

    @Autowired
    public void configureDatabase(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userDetailsDatabaseService);
    }

    /*@Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }*/
}
