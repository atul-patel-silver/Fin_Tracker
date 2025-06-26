package org.service.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RoleDto {

    private Long id;
    @NotEmpty(message = "Role Name can not be null or empty")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Only character values are allowed")
    private String name;

    private String code;
    private String description;

    private Boolean isActive;

    private Boolean isDeleted;
    boolean isDefault;
}
