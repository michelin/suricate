package io.suricate.monitoring.model.entities;

import io.suricate.monitoring.model.entities.generic.AbstractAuditingEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Token entity
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Token extends AbstractAuditingEntity<Long> {
    /**
     * The category id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The token name
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * The owner
     */
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "userId", referencedColumnName = "ID")
    private User user;
}
