package com.michelin.suricate.services.api;

import com.michelin.suricate.model.entities.PersonalAccessToken;
import com.michelin.suricate.model.entities.User;
import com.michelin.suricate.repositories.PersonalAccessTokenRepository;
import com.michelin.suricate.security.LocalUser;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Personal access token service.
 */
@Service
public class PersonalAccessTokenService {
    @Autowired
    private PersonalAccessTokenRepository personalAccessTokenRepository;

    /**
     * Get all user tokens.
     *
     * @param user The user
     * @return The user tokens
     */
    @Transactional(readOnly = true)
    public List<PersonalAccessToken> findAllByUser(User user) {
        return personalAccessTokenRepository.findAllByUser(user);
    }

    /**
     * Find a token by given name and user.
     *
     * @param name The token name
     * @param user The user
     * @return The token
     */
    @Transactional(readOnly = true)
    public Optional<PersonalAccessToken> findByNameAndUser(String name, User user) {
        return personalAccessTokenRepository.findByNameAndUser(name, user);
    }

    /**
     * Find a token by given checksum.
     *
     * @param checksum The token checksum
     * @return The token
     */
    @Transactional(readOnly = true)
    public Optional<PersonalAccessToken> findByChecksum(Long checksum) {
        return personalAccessTokenRepository.findByChecksum(checksum);
    }

    /**
     * Create a JWT token.
     *
     * @param tokenName     The token name
     * @param checksum      The token checksum
     * @param connectedUser The authenticated user
     */
    @Transactional
    public PersonalAccessToken create(String tokenName, Long checksum, LocalUser connectedUser) {
        PersonalAccessToken personalAccessToken = new PersonalAccessToken();
        personalAccessToken.setName(tokenName);
        personalAccessToken.setChecksum(checksum);
        personalAccessToken.setUser(connectedUser.getUser());

        return personalAccessTokenRepository.save(personalAccessToken);
    }

    /**
     * Delete a token by id.
     *
     * @param id The token id
     */
    public void deleteById(Long id) {
        personalAccessTokenRepository.deleteById(id);
    }
}
