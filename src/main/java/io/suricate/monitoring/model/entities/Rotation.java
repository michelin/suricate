package io.suricate.monitoring.model.entities;

import io.suricate.monitoring.model.entities.generic.AbstractAuditingEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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
     * Does the progress bar should be displayed
     */
    @Column(nullable = false)
    @Type(type = "yes_no")
    private boolean progressBar;

    /**
     * The rotation token
     */
    @Column(nullable = false)
    private String token;

    /**
     * The list of related rotations
     */
    @OneToMany(mappedBy = "rotation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RotationProject> rotationProjects = new ArrayList<>();

    /**
     * The list of users of the rotation
     */
    @ManyToMany
    @JoinTable(name = "user_rotation", joinColumns = {@JoinColumn(name = "rotation_id")}, inverseJoinColumns = {@JoinColumn(name = "user_id")})
    private Set<User> users = new LinkedHashSet<>();
}
