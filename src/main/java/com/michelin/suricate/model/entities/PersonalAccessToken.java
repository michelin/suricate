package com.michelin.suricate.model.entities;

import com.michelin.suricate.model.entities.generic.AbstractAuditingEntity;
import lombok.*;

import javax.persistence.*;

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

    @Override
    public int hashCode() { return super.hashCode(); }

    @Override
    public boolean equals(Object other) { return super.equals(other); }
}
