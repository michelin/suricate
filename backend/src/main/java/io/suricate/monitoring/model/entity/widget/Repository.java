package io.suricate.monitoring.model.entity.widget;

import io.suricate.monitoring.model.enums.RepositoryTypeEnum;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * Describe a repository
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Repository {

    /**
     * The repository name
     */
    @Id
    private String name;

    /**
     * The repository url
     */
    @Column(nullable = false)
    private String url;

    /**
     * The repository branch to clone
     */
    @Column(nullable = false)
    private String branch;

    /**
     * If the repository is enable or not
     */
    @Column(nullable = false)
    @Type(type = "yes_no")
    private boolean enabled = true;

    /**
     * The type of the repository
     */
    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private RepositoryTypeEnum type;
}
