package org.service.user.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.service.user.model.Role;

import java.util.HashSet;
import java.util.Set;

@Data
public class UserDTO {
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(max = 50, message = "Middle name must be at most 50 characters")
    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String emailId;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\d{10,15}$", message = "Mobile number must be between 10 and 15 digits")
    private String mobileNumber;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
    private String userName;

    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,100}$",
        message = "Password must be 8-100 characters, include upper and lower case letters, a number, and a special character"
    )
    private String password;

    private String sub;

    private String loginType;

    boolean isActive ;

    boolean isDeleted ;
}