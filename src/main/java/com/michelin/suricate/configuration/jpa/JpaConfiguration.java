package com.michelin.suricate.configuration.jpa;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA Configuration.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaConfiguration {
    /**
     * Auditor configuration.
     *
     * @return The auditor configuration
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new JpaAuditorAware();
    }
}
