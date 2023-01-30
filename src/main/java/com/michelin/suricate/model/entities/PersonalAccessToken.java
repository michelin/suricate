package com.michelin.suricate.model.entities;

import com.michelin.suricate.model.entities.generic.AbstractAuditingEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * PersonalAccessToken entity
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class PersonalAccessToken extends AbstractAuditingEntity<Long> {
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
     * The checksum of the generated token
     */
    @Column(nullable = false, unique = true)
    private Long checksum;

    /**
     * The owner
     */
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "userId", referencedColumnName = "ID")
    private User user;
}
