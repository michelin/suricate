package io.suricate.monitoring.configuration.security.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(name = "application.authentication.provider", havingValue = "database")
public class DatabaseAuthentication {

    private final DataSource dataSource;

    @Autowired
    public DatabaseAuthentication(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Autowired
    public void configureDatabase(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .jdbcAuthentication()
                .dataSource(dataSource)
            .authoritiesByUsernameQuery("select u.username, r.name from user u, user_role ur, role r where ur.user_id = u.id and ur.role_id = r.id and u.username = ?")
            .usersByUsernameQuery("select username, password as password,1 FROM user where username = ?");
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(25);
    }
}
