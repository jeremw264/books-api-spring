package com.jeremw.bookstore.api.auth.dto;

import com.jeremw.bookstore.api.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing authentication information.
 *
 * <p>
 * This class contains information about a user's authentication, including the associated
 * user, access token, and refresh token.
 * </p>
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthDto {

	/**
	 * The user associated with the authentication information.
	 */
	private User user;

	/**
	 * The access token used for authentication.
	 */
	private String accessToken;

	/**
	 * The refresh token used for obtaining a new access token.
	 */
	private String refreshToken;

}

