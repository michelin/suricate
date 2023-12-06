package com.michelin.suricate.configuration.jpa;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * JPA Auditor Aware.
 */
public class JpaAuditorAware implements AuditorAware<String> {
    /**
     * Get the current logged-in user.
     *
     * @return The auditor username
     */
    @NotNull
    @Override
    public Optional<String> getCurrentAuditor() {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return Optional.of("APPLICATION");
        }

        return Optional.of((SecurityContextHolder.getContext().getAuthentication().getName()));
    }
}
