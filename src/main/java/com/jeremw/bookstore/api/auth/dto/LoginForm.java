package com.jeremw.bookstore.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing user login information.
 *
 * <p>
 * This class contains the username and password for user authentication.
 * </p>
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Data
@Builder
@AllArgsConstructor
public class LoginForm {

	/**
	 * The username for user authentication.
	 */
	@NotBlank(message = "Username cannot be blank")
	@Schema(description = "The username for user authentication")
	private String username;

	/**
	 * The password for user authentication.
	 */
	@NotBlank(message = "Password cannot be blank")
	@Schema(description = "The password for user authentication")
	private String password;

}

