package io.suricate.monitoring.services.api;

import io.suricate.monitoring.model.entities.PersonalAccessToken;
import io.suricate.monitoring.model.entities.User;
import io.suricate.monitoring.repositories.PersonalAccessTokenRepository;
import io.suricate.monitoring.security.LocalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PersonalAccessTokenService {
    @Autowired
    private PersonalAccessTokenRepository personalAccessTokenRepository;

    /**
     * Get all user tokens
     * @param user The user
     * @return The user tokens
     */
    @Transactional(readOnly = true)
    public List<PersonalAccessToken> findAllByUser(User user) {
        return personalAccessTokenRepository.findAllByUser(user);
    }

    /**
     * Find a token by given name and user
     * @param name The token name
     * @param user The user
     * @return The token
     */
    @Transactional(readOnly = true)
    public Optional<PersonalAccessToken> findByNameAndUser(String name, User user) {
        return personalAccessTokenRepository.findByNameAndUser(name, user);
    }

    /**
     * Find a token by given checksum
     * @param checksum The token checksum
     * @return The token
     */
    @Transactional(readOnly = true)
    public Optional<PersonalAccessToken> findByChecksum(Long checksum) {
        return personalAccessTokenRepository.findByChecksum(checksum);
    }

    /**
     * Create a JWT token
     * @param tokenName The token name
     * @param checksum The token checksum
     */
    @Transactional
    public PersonalAccessToken create(String tokenName, Long checksum, Authentication authentication) {
        PersonalAccessToken personalAccessToken = new PersonalAccessToken();
        personalAccessToken.setName(tokenName);
        personalAccessToken.setChecksum(checksum);
        personalAccessToken.setUser(((LocalUser) authentication.getPrincipal()).getUser());

        return personalAccessTokenRepository.save(personalAccessToken);
    }

    /**
     * Delete a token by id
     * @param id The token id
     */
    public void deleteById(Long id) {
        personalAccessTokenRepository.deleteById(id);
    }
}
