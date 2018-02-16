package io.suricate.monitoring.configuration.jpa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA configurations
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JPAConfiguration{

    /**
     * Auditor configuration
     *
     * @return The auditor configuration
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new JPAAuditorAwareImpl();
    }
}
