package com.jeremw.bookstore.api.auth;

import com.jeremw.bookstore.api.user.User;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

/**
 * Service interface for handling JSON Web Token (JWT) operations.
 *
 * <p>
 * This service provides methods for extracting, generating, validating, and handling JWT
 * tokens.
 * </p>
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Service
public interface JwtService {

	/**
	 * Extracts the username from the provided JWT token.
	 *
	 * @param token The JWT token from which to extract the username.
	 * @return The username extracted from the JWT token.
	 */
	String extractUsername(String token);

	/**
	 * Generates a JWT token for the provided user.
	 *
	 * @param user The user for whom the JWT token is generated.
	 * @return The generated JWT token.
	 */
	String generateToken(User user);

	/**
	 * Checks if the provided JWT token is valid for the specified user.
	 *
	 * @param token The JWT token to validate.
	 * @param user  The user against whom to validate the token.
	 * @return {@code true} if the token is valid for the user, {@code false} otherwise.
	 */
	boolean isTokenValid(String token, User user);

	/**
	 * Generates a ResponseCookie containing the provided JWT token.
	 *
	 * @param jwt The JWT token to be included in the cookie.
	 * @return A ResponseCookie containing the JWT token.
	 */
	ResponseCookie generateJwtCookie(String jwt);

	/**
	 * Retrieves the JWT token from cookies in the HttpServletRequest.
	 *
	 * @param request The HttpServletRequest from which to retrieve the JWT token.
	 * @return The JWT token retrieved from cookies, or {@code null} if not found.
	 */
	String getJwtFromCookies(HttpServletRequest request);

	/**
	 * Generates a clean (empty) JWT cookie for logout or invalidation purposes.
	 *
	 * @return A ResponseCookie with an empty value for JWT token.
	 */
	ResponseCookie getCleanJwtCookie();

}


