package io.suricate.monitoring.model.entities;

import io.suricate.monitoring.model.entities.generic.AbstractEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * Rotating screen entity
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class RotatingScreen extends AbstractEntity<Long> {

    /**
     * The id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The rotation speed (in seconds) of the screen
     */
    @Column
    private Integer rotationSpeed;

    /**
     * The related project
     */
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "projectId", referencedColumnName = "ID")
    private Project project;
}
