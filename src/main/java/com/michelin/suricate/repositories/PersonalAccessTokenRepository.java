package com.michelin.suricate.repositories;

import com.michelin.suricate.model.entities.PersonalAccessToken;
import com.michelin.suricate.model.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalAccessTokenRepository extends CrudRepository<PersonalAccessToken, Long>, JpaSpecificationExecutor<PersonalAccessToken> {
    /**
     * Find all tokens of given user
     * @param user The user
     * @return The user tokens
     */
    List<PersonalAccessToken> findAllByUser(User user);

    /**
     * Find a token by given name and user
     * @param name The token name
     * @param user The user
     * @return The token
     */
    Optional<PersonalAccessToken> findByNameAndUser(String name, User user);

    /**
     * Find a token by given checksum
     * @param checksum The token checksum
     * @return The token
     */
    @EntityGraph(attributePaths = "user.roles")
    Optional<PersonalAccessToken> findByChecksum(Long checksum);
}
