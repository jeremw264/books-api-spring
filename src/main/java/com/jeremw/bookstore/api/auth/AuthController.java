package com.jeremw.bookstore.api.auth;

import com.jeremw.bookstore.api.auth.dto.LoginForm;
import com.jeremw.bookstore.api.auth.dto.RegisterForm;
import com.jeremw.bookstore.api.user.UserResourceException;
import com.jeremw.bookstore.api.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller interface for handling user authentication operations.
 *
 * <p>
 * This controller provides endpoints for user login, registration, token refresh, and
 * logout.
 * </p>
 *

 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Tag(name = "Authentication Endpoint")
@RestController
@RequestMapping("/auth")
public interface AuthController {

	/**
	 * Logs in a user with the provided login form.
	 *
	 * @param loginForm The login form containing the username and password.
	 * @return ResponseEntity containing the user information and authentication tokens as cookies.
	 * @throws UserResourceException If there is an error retrieving user information.
	 * @throws AuthResourceException If there is an error during the authentication process.
	 */
	@Operation(summary = "User Login", description = "Endpoint for user login.")
	@ApiResponse(responseCode = "200", description = "Successful login",
			content = @Content(schema = @Schema(implementation = UserDto.class)))
	@PostMapping("/login")
	ResponseEntity<UserDto> login(@Valid @RequestBody LoginForm loginForm)
			throws UserResourceException, AuthResourceException;

	/**
	 * Registers a new user with the provided registration form.
	 *
	 * @param registerForm The registration form containing the username, email, and password.
	 * @return ResponseEntity containing the registered user information.
	 * @throws UserResourceException If there is an error registering the user.
	 */
	@Operation(summary = "User Registration", description = "Endpoint for user registration.")
	@ApiResponse(responseCode = "201", description = "Successful registration",
			content = @Content(schema = @Schema(implementation = UserDto.class)))
	@PostMapping("/register")
	ResponseEntity<UserDto> register(@RequestBody RegisterForm registerForm) throws UserResourceException;

	/**
	 * Refreshes the authentication token using the refresh token from the request cookies.
	 *
	 * @param request The HTTPServletRequest containing the refresh token as a cookie.
	 * @return ResponseEntity containing the new authentication token as a cookie.
	 * @throws UserResourceException         If there is an error retrieving user information.
	 * @throws RefreshTokenResourceException If there is an error during the token refresh process.
	 */
	@Operation(summary = "Refresh User Tokens", description = "Endpoint for refreshing user tokens.")
	@ApiResponse(responseCode = "200", description = "User tokens successfully refreshed",
			content = @Content(schema = @Schema(implementation = UserDto.class)))
	@PostMapping("/refresh")
	ResponseEntity<UserDto> refresh(HttpServletRequest request)
			throws UserResourceException, RefreshTokenResourceException;

	/**
	 * Logs out the user by deleting the refresh token from the request cookies.
	 *
	 * @param request The HTTPServletRequest containing the refresh token as a cookie.
	 * @return ResponseEntity indicating successful logout.
	 * @throws RefreshTokenResourceException If there is an error during the logout process.
	 */
	@Operation(summary = "User Logout", description = "Endpoint for user logout.")
	@ApiResponse(responseCode = "200", description = "User successfully logged out")
	@PostMapping("/logout")
	ResponseEntity<Void> logout(HttpServletRequest request) throws RefreshTokenResourceException;

}

