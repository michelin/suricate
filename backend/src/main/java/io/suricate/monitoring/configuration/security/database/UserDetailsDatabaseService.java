package io.suricate.monitoring.configuration.security.database;

import io.suricate.monitoring.configuration.security.ConnectedUser;
import io.suricate.monitoring.model.entity.user.User;
import io.suricate.monitoring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("userDetailsService")
@ConditionalOnProperty(name = "application.authentication.provider", havingValue = "database")
public class UserDetailsDatabaseService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsDatabaseService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> currentUser = userRepository.findByUsernameIgnoreCase(username);

        if(!currentUser.isPresent()) {
            throw new UsernameNotFoundException("The specified user has not been found");
        }

        Collection<? extends GrantedAuthority> authorities = currentUser
                                                                    .map(user -> user.getRoles().stream()
                                                                        .map( roles -> new SimpleGrantedAuthority(roles.getName()))
                                                                        .collect(Collectors.toList()))
                                                                    .orElseThrow(() -> new UsernameNotFoundException("User " + username + " was not authorized"));

        return new ConnectedUser(currentUser.get(), authorities);
    }
}
