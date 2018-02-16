package io.suricate.monitoring.configuration.jpa;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class JPAAuditorAwareImpl implements AuditorAware<String> {

    /** Get the current logged in user */
    @Override
    public String getCurrentAuditor() {
        if(SecurityContextHolder.getContext().getAuthentication() == null) {
            return "APPLICATION";
        }

        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    }
}
