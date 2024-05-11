package com.jeremw.bookstore.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing user registration information.
 *
 * <p>
 * This class contains the username, email, and password for user registration.
 * </p>
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Data
@Builder
public class RegisterForm {

	/**
	 * The username for user registration.
	 */
	@NotBlank(message = "Username cannot be blank")
	@Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
	@Schema(description = "The username for user registration")
	private String username;

	/**
	 * The email address for user registration.
	 */
	@NotBlank(message = "Email cannot be blank")
	@Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "Invalid email format")
	@Schema(description = "The email address for user registration")
	private String email;

	/**
	 * The password for user registration.
	 */
	@NotBlank(message = "Password cannot be blank")
	@Size(min = 8, message = "Password must be at least 8 characters")
	@Schema(description = "The password for user registration")
	private String password;

}
