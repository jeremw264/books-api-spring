package com.jeremw.bookstore.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * Data Transfer Object (DTO) class representing a simplified view of a user.
 *
 * <p>
 * This class provides a simplified representation of a user entity for data transfer operations.
 * It includes basic user information such as id, username, and email address.
 * </p>
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Data
@Builder
@Schema(description = "DTO representing a simplified view of a user")
public class UserDto {

	/**
	 * The id of the user.
	 */
	@Schema(description = "The unique identifier of the user")
	private Long id;

	/**
	 * The username of the user.
	 */
	@Schema(description = "The username of the user")
	private String username;

	/**
	 * The email address of the user.
	 */
	@Schema(description = "The email address of the user")
	private String email;

}
