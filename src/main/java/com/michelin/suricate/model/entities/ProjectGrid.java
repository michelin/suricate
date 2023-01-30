package com.michelin.suricate.model.entities;

import com.michelin.suricate.model.entities.generic.AbstractAuditingEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ProjectGrid extends AbstractAuditingEntity<Long> {
    /**
     * The project id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Number of grid
     */
    @Column
    private Integer time;

    /**
     * The related project
     */
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "projectId", referencedColumnName = "ID")
    private Project project;

    /**
     * The list of related widgets
     */
    @OneToMany(mappedBy = "projectGrid", cascade = CascadeType.REMOVE)
    private Set<ProjectWidget> widgets = new LinkedHashSet<>();
}
