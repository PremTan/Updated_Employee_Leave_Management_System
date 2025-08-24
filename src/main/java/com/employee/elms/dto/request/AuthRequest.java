package com.employee.elms.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
	
	@Email(message = "Invalid email format")
    private String email;
	
	@NotBlank(message = "Password is required")
    private String password;
}
