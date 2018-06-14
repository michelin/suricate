package io.suricate.monitoring.repository;

import io.suricate.monitoring.model.entity.setting.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository used for request UserSettings in database
 */
public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {
}
