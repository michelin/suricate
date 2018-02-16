package io.suricate.monitoring.configuration.jpa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JPAConfiguration{

    /** Use for retrieve the user to Audit */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new JPAAuditorAwareImpl();
    }
}
