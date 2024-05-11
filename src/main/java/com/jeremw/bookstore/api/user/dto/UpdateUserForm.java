package com.jeremw.bookstore.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Data transfer object (DTO) representing the form used to update a user's information.
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class UpdateUserForm {

	@Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", message = "The email is not valid")
	@Schema(description = "The updated email of the user")
	private String email;

	@Schema(description = "The updated password of the user")
	private String password;

}
