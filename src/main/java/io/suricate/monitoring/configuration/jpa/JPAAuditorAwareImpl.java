package io.suricate.monitoring.configuration.jpa;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Update auditing column at each request
 */
public class JPAAuditorAwareImpl implements AuditorAware<String> {

    /**
     * Get the current logged in user
     *
     * @return The auditor username
     */
    @NotNull
    @Override
    public Optional<String> getCurrentAuditor() {
        if(SecurityContextHolder.getContext().getAuthentication() == null) {
            return Optional.of("APPLICATION");
        }

        return Optional.of(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    }
}
