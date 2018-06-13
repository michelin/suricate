package io.suricate.monitoring.model.entity.setting;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Contains every possible value for a setting
 */
@Entity(name = "allowed_setting_value")
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(callSuper = false) @ToString
public class AllowedSettingValue {

    /**
     * The setting id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The title to display for the user
     */
    @Column(nullable = false)
    private String title;

    /**
     * The value of the entry (used in the code)
     */
    @Column(nullable = false)
    private String value;

    /**
     * The related setting
     */
    @ManyToOne
    @NotNull
    @JoinColumn(name = "setting_id", referencedColumnName = "id", nullable = false)
    private Setting setting;
}
