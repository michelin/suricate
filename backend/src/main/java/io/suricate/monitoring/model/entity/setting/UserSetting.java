package io.suricate.monitoring.model.entity.setting;

import io.suricate.monitoring.model.entity.user.User;

import javax.persistence.*;

/**
 * Class linked the table user and settings
 */
@Entity(name = "user_setting")
public class UserSetting {

    /**
     * The user setting id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * The related user
     */
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    /**
     * The setting reference
     */
    @OneToOne
    @JoinColumn(name = "setting_id", nullable = false)
    private Setting setting;

    /**
     * The allowed setting value
     */
    @OneToOne
    @JoinColumn(name = "allowed_setting_value_id")
    private AllowedSettingValue settingValue;

    /**
     * The unconstrained value
     */
    @Column(name = "unconstrained_value")
    private String unconstrainedValue;
}
