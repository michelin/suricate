package io.suricate.monitoring.configuration.jpa;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

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
    public String getCurrentAuditor() {
        if(SecurityContextHolder.getContext().getAuthentication() == null) {
            return "APPLICATION";
        }

        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    }
}
