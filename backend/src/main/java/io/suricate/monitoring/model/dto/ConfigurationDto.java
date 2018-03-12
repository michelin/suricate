package io.suricate.monitoring.model.dto;


public class ConfigurationDto {

    /**
     * The configuration key
     */
    private String key;
    /**
     * The configuration value
     */
    private String value;
    /**
     * Export
     */
    private boolean export;

    /**
     * Constructor
     */
    public ConfigurationDto() {}

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public boolean isExport() {
        return export;
    }
    public void setExport(boolean export) {
        this.export = export;
    }
}
