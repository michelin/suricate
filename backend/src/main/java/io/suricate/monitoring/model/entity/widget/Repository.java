package io.suricate.monitoring.model.entity.widget;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

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
     * The login to use for the connection to the remote repository
     */
    @Column
    private String login;

    /**
     * The password to use for the connection to the remote repository
     */
    @Column
    private String password;

    /**
     * If the repository is enable or not
     */
    @Column(nullable = false)
    @Type(type = "yes_no")
    private boolean enabled = true;

    /**
     * The list of widgets for this repository
     */
    @OneToMany(mappedBy = "repository")
    private List<Widget> widgets = new ArrayList<>();
}
