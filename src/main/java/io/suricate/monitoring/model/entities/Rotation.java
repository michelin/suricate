package io.suricate.monitoring.model.entities;

import io.suricate.monitoring.model.entities.generic.AbstractAuditingEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Rotation entity
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Rotation extends AbstractAuditingEntity<Long> {
    /**
     * The rotation id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The rotation name
     */
    @Column(nullable = false)
    private String name;

    /**
     * The list of related rotations
     */
    @OneToMany(mappedBy = "rotation", cascade = CascadeType.REMOVE)
    private Set<RotationProject> rotationProjects = new LinkedHashSet<>();
}
