package io.suricate.monitoring.model.entity.setting;

import io.suricate.monitoring.model.enums.SettingDataType;
import io.suricate.monitoring.service.api.UserService;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Constains every setting to display
 */
@Entity
@Getter @Setter @NoArgsConstructor @EqualsAndHashCode(callSuper = false) @ToString
public class Setting {
    /**
     * The setting id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The setting name/description
     */
    @Column(nullable = false)
    private String description;

    /**
     * Tell if the settings have constrained values
     */
    @Column(nullable = false)
    @Type(type = "yes_no")
    private boolean constrained;

    /**
     * The setting data type
     */
    @Column(nullable = false, name = "data_type")
    @Enumerated(EnumType.STRING)
    private SettingDataType dataType;

    /**
     * Hold the possible values (if we have a select setting for example)
     */
    @OneToMany(mappedBy = "setting")
    private List<AllowedSettingValue> allowedSettingValue = new ArrayList<>();
}
