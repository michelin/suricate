package io.suricate.monitoring.model.dto.widget;

import io.suricate.monitoring.model.enums.WidgetVariableType;

import java.util.ArrayList;
import java.util.List;

public class WidgetParamResponse {
    private String name;

    private String description;

    private String defaultValue;

    private WidgetVariableType type;

    private String acceptFileRegex;

    private String usageExample;

    private boolean required = true;

    private List<WidgetParamValueResponse> values = new ArrayList<>();


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public WidgetVariableType getType() {
        return type;
    }
    public void setType(WidgetVariableType type) {
        this.type = type;
    }

    public String getAcceptFileRegex() {
        return acceptFileRegex;
    }
    public void setAcceptFileRegex(String acceptFileRegex) {
        this.acceptFileRegex = acceptFileRegex;
    }

    public String getUsageExample() {
        return usageExample;
    }
    public void setUsageExample(String usageExample) {
        this.usageExample = usageExample;
    }

    public boolean isRequired() {
        return required;
    }
    public void setRequired(boolean required) {
        this.required = required;
    }

    public List<WidgetParamValueResponse> getValues() {
        return values;
    }
    public void setValues(List<WidgetParamValueResponse> values) {
        this.values = values;
    }
}
