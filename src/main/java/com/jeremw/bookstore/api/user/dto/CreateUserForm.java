package com.jeremw.bookstore.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * Data transfer object (DTO) representing the form used to create a new user.
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Data
@Builder
public class CreateUserForm {

	@NotBlank(message = "The username is required to create a new user")
	@Schema(description = "The username of the new user")
	private String username;

	@NotBlank(message = "The email is required to create a new user")
	@Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "Invalid email format")
	@Schema(description = "The password of the new user")
	private String email;

	@NotBlank(message = "The password is required to create a new user")
	@Schema(description = "The password of the new user")
	private String password;

}
