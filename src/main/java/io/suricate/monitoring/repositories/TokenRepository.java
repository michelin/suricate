package io.suricate.monitoring.repositories;

import io.suricate.monitoring.model.entities.Token;
import io.suricate.monitoring.model.entities.User;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends CrudRepository<Token, Long>, JpaSpecificationExecutor<Token> {
    /**
     * Find all tokens of given user
     * @param user The user
     * @return The user tokens
     */
    List<Token> findAllByUser(User user);
}
