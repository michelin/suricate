package io.suricate.monitoring.model.dto.project;

public class ProjectDto {
    private Long id;
    private String name;
    private Integer maxColumn;
    private Integer widgetHeight;
    private String cssStyle;
    private String token;

    public ProjectDto() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxColumn() {
        return maxColumn;
    }
    public void setMaxColumn(Integer maxColumn) {
        this.maxColumn = maxColumn;
    }

    public Integer getWidgetHeight() {
        return widgetHeight;
    }
    public void setWidgetHeight(Integer widgetHeight) {
        this.widgetHeight = widgetHeight;
    }

    public String getCssStyle() {
        return cssStyle;
    }
    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
