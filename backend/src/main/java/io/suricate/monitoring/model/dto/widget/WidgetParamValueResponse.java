package io.suricate.monitoring.model.dto.widget;

public class WidgetParamValueResponse {
    private String jsKey;
    private String value;

    public String getJsKey() {
        return jsKey;
    }
    public void setJsKey(String jsKey) {
        this.jsKey = jsKey;
    }

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
