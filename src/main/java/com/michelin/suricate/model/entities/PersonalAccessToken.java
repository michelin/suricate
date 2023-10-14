package com.michelin.suricate.model.entities;

import com.michelin.suricate.model.entities.generic.AbstractAuditingEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Personal access token entity.
 */
@Entity
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class PersonalAccessToken extends AbstractAuditingEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private Long checksum;

    @ToString.Exclude
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "userId", referencedColumnName = "ID")
    private User user;

    /**
     * Hashcode method.
     * Do not use lombok @EqualsAndHashCode method as it calls super method
     * then call the self-defined child Hashcode method
     *
     * @return The hash code
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Equals method.
     * Do not use lombok @EqualsAndHashCode method as it calls super method
     * then call the self-defined child Equals method
     *
     * @param other The other object to compare
     * @return true if equals, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        return super.equals(other);
    }
}
