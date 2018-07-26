package io.suricate.monitoring.configuration.jpa;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Class used for update Auditing column at each request
 */
public class JPAAuditorAwareImpl implements AuditorAware<String> {

    /**
     * Get the current logged in user
     *
     * @return The auditor username
     */
    @Override
    public Optional<String> getCurrentAuditor() {
        if(SecurityContextHolder.getContext().getAuthentication() == null) {
            return Optional.of("APPLICATION");
        }

        return Optional.of(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    }
}
