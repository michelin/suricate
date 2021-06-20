package io.suricate.monitoring.model.entities;

import io.suricate.monitoring.model.entities.generic.AbstractAuditingEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * RotationProject entity
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class RotationProject extends AbstractAuditingEntity<Long> {
    /**
     * The rotation project id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Rotation speed
     */
    @Column
    private int rotationSpeed;

    /**
     * Rotation
     */
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "rotationId", referencedColumnName = "ID")
    private Rotation rotation;

    /**
     * Project
     */
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "projectId", referencedColumnName = "ID")
    private Project project;
}
