package org.service.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginDto {

    @NotEmpty(message = "UserName Name can not be a null or empty")
    @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
    String userName;


    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,100}$",
            message = "Password must be 8-100 characters, include upper and lower case letters, a number, and a special character"
    )
    String password;
}
