package com.michelin.suricate.model.entities;

import com.michelin.suricate.model.entities.generic.AbstractAuditingEntity;
import lombok.*;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class ProjectGrid extends AbstractAuditingEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer time;

    @ToString.Exclude
    @ManyToOne
    @PrimaryKeyJoinColumn(name = "projectId", referencedColumnName = "ID")
    private Project project;

    @ToString.Exclude
    @OneToMany(mappedBy = "projectGrid", cascade = CascadeType.REMOVE)
    private Set<ProjectWidget> widgets = new LinkedHashSet<>();

    @Override
    public int hashCode() { return super.hashCode(); }

    @Override
    public boolean equals(Object other) { return super.equals(other); }
}
