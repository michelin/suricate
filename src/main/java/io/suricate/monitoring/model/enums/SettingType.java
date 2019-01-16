package io.suricate.monitoring.model.enums;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Hold every settings types
 */
public enum SettingType {
    TEMPLATE,
    LANGUAGE;


    public static SettingType getSettingTypeByString(final String settingTypeString) {
        return Arrays.stream(SettingType.values())
            .filter(settingType -> settingTypeString.equalsIgnoreCase(settingType.name()))
            .collect(Collectors.toList())
            .get(0);
    }
}
