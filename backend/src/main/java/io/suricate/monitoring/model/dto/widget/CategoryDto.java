package io.suricate.monitoring.model.dto.widget;

import io.suricate.monitoring.model.entity.Asset;
import io.suricate.monitoring.model.entity.widget.Category;
import io.suricate.monitoring.model.entity.widget.Widget;

import java.util.ArrayList;
import java.util.List;

public class CategoryDto {
    private Long id;
    private String name;
    private String technicalName;
    private Asset image;

    public CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.technicalName = category.getTechnicalName();
        this.image = category.getImage();
    }

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

    public String getTechnicalName() {
        return technicalName;
    }
    public void setTechnicalName(String technicalName) {
        this.technicalName = technicalName;
    }

    public Asset getImage() {
        return image;
    }
    public void setImage(Asset image) {
        this.image = image;
    }
}
