package io.suricate.monitoring.model.dto.user;

import io.suricate.monitoring.model.entity.user.Role;

public class RoleDto {

    private String name;
    private String description;

    public RoleDto() {}

    public RoleDto(Role role) {
        this.name = role.getName();
        this.description = role.getDescription();
    }

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
}
