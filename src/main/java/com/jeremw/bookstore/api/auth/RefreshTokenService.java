package com.jeremw.bookstore.api.auth;

import com.jeremw.bookstore.api.auth.dto.AuthDto;
import com.jeremw.bookstore.api.user.User;
import com.jeremw.bookstore.api.user.UserResourceException;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

/**
 * Service interface for managing refresh tokens and related operations.
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Service
public interface RefreshTokenService {

	/**
	 * Creates a new refresh token for the specified user.
	 *
	 * @param user The user for whom the refresh token is created.
	 * @return The created refresh token.
	 */
	RefreshToken createRefreshToken(User user);

	/**
	 * Verifies if the given refresh token has expired.
	 *
	 * @param token The refresh token to verify.
	 * @throws RefreshTokenResourceException If the token is null or has expired.
	 */
	void verifyExpiration(RefreshToken token) throws RefreshTokenResourceException;

	/**
	 * Generates a new JWT token based on a valid refresh token.
	 *
	 * @param refreshTokenString The token value of the valid refresh token.
	 * @return An AuthDto containing the user, new access token, and the original refresh
	 * token.
	 * @throws RefreshTokenResourceException If the refresh token is not found or has
	 *                                       expired.
	 * @throws UserResourceException         If the user associated with the refresh token is not
	 *                                       found.
	 */
	AuthDto generateNewToken(String refreshTokenString) throws RefreshTokenResourceException, UserResourceException;

	/**
	 * Finds a refresh token by its token value.
	 *
	 * @param token The token value of the refresh token to find.
	 * @return The found refresh token.
	 * @throws RefreshTokenResourceException If the refresh token is not found.
	 */
	RefreshToken findByToken(String token) throws RefreshTokenResourceException;

	/**
	 * Generates a ResponseCookie containing the provided refresh token.
	 *
	 * @param token The refresh token to be included in the cookie.
	 * @return A ResponseCookie containing the refresh token.
	 */
	ResponseCookie generateRefreshTokenCookie(String token);

	/**
	 * Retrieves the refresh token from cookies in the HttpServletRequest.
	 *
	 * @param request The HttpServletRequest from which to retrieve the refresh token.
	 * @return The refresh token retrieved from cookies, or an empty string if not found.
	 */
	String getRefreshTokenFromCookies(HttpServletRequest request);

	/**
	 * Deletes a refresh token by its token value.
	 *
	 * @param token The token value of the refresh token to delete.
	 * @throws RefreshTokenResourceException If the refresh token is not found.
	 */
	void deleteByToken(String token) throws RefreshTokenResourceException;

	/**
	 * Generates a clean (empty) refresh token cookie for logout or invalidation purposes.
	 *
	 * @return A ResponseCookie with an empty value for the refresh token.
	 */
	ResponseCookie getCleanRefreshTokenCookie();

}

