package com.jeremw.bookstore.api.auth;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.jeremw.bookstore.api.auth.dto.AuthDto;
import com.jeremw.bookstore.api.user.User;
import com.jeremw.bookstore.api.user.UserResourceException;
import com.jeremw.bookstore.api.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

/**
 * Service implementation for managing refresh tokens and related operations.
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;

	private final JwtService jwtService;

	private final UserService userService;

	@Value("${auth.refresh-token.name}")
	private String refreshTokenName;

	@Value("${auth.refresh-token.secret}")
	private String refreshTokenSecret;

	@Value("${auth.refresh-token.expiration-msec}")
	private Long refreshTokenExpiration;

	/**
	 * Creates a new refresh token for the specified user.
	 *
	 * @param user The user for whom the refresh token is created.
	 * @return The created refresh token.
	 */
	@Override
	public RefreshToken createRefreshToken(User user) {
		log.info("Creating refresh token for user: {}", user.getUsername());
		RefreshToken refreshToken = RefreshToken.builder()
				.revoked(false)
				.user(user)
				.token(Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes()))
				.expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
				.build();
		return refreshTokenRepository.save(refreshToken);
	}

	/**
	 * Verifies if the given refresh token has expired.
	 *
	 * @param token The refresh token to verify.
	 * @throws RefreshTokenResourceException If the token is null or has expired.
	 */
	public void verifyExpiration(RefreshToken token) throws RefreshTokenResourceException {
		if (token == null) {
			log.error("Token is null");
			throw new RefreshTokenResourceException("NullToken", "Token is null", HttpStatus.BAD_REQUEST);
		}
		if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
			log.error("Refresh token expired for user: {}", token.getUser().getUsername());
			refreshTokenRepository.delete(token);
			throw new RefreshTokenResourceException("ExpiredRefreshToken",
					"Refresh token was expired. Please make a new authentication request", HttpStatus.UNAUTHORIZED);
		}
	}

	/**
	 * Finds a refresh token by its token value.
	 *
	 * @param token The token value of the refresh token to find.
	 * @return The found refresh token.
	 * @throws RefreshTokenResourceException If the refresh token is not found.
	 */
	@Override
	public RefreshToken findByToken(String token) throws RefreshTokenResourceException {
		log.info("Finding refresh token by token: {}", token);
		return refreshTokenRepository.findByToken(token)
				.orElseThrow(() -> new RefreshTokenResourceException("RefreshTokenNotFound",
						"The refresh token is not found.", HttpStatus.NOT_FOUND));
	}

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
	public AuthDto generateNewToken(String refreshTokenString)
			throws RefreshTokenResourceException, UserResourceException {
		RefreshToken refreshToken = findByToken(refreshTokenString);
		String username = refreshToken.getUser().getUsername();

		verifyExpiration(refreshToken);

		User user = userService.findUserByUsername(username);
		String token = jwtService.generateToken(user);

		log.debug("Generated new JWT token for user: {}", user.getUsername());

		return AuthDto.builder().user(user).accessToken(token).refreshToken(refreshTokenString).build();
	}

	/**
	 * Generates a ResponseCookie containing the provided refresh token.
	 *
	 * @param token The refresh token to be included in the cookie.
	 * @return A ResponseCookie containing the refresh token.
	 */
	@Override
	public ResponseCookie generateRefreshTokenCookie(String token) {
		log.debug("Generating refresh token cookie");
		return ResponseCookie.from(refreshTokenName, token)
				.path("/")
				.maxAge(TimeUnit.SECONDS.toSeconds(refreshTokenExpiration))
				.httpOnly(true)
				.secure(true)
				.sameSite("None")
				.build();
	}

	/**
	 * Retrieves the refresh token from cookies in the HttpServletRequest.
	 *
	 * @param request The HttpServletRequest from which to retrieve the refresh token.
	 * @return The refresh token retrieved from cookies, or an empty string if not found.
	 */
	@Override
	public String getRefreshTokenFromCookies(HttpServletRequest request) {
		Cookie cookie = WebUtils.getCookie(request, refreshTokenName);
		if (cookie != null) {
			log.debug("Retrieved refresh token from cookies");
			return cookie.getValue();
		}
		else {
			log.debug("Refresh token not found in cookies");
			return "";
		}
	}

	/**
	 * Deletes a refresh token by its token value.
	 *
	 * @param token The token value of the refresh token to delete.
	 * @throws RefreshTokenResourceException If the refresh token is not found.
	 */
	@Override
	public void deleteByToken(String token) throws RefreshTokenResourceException {
		log.info("Deleting refresh token by token: {}", token);
		refreshTokenRepository.delete(findByToken(token));
	}

	/**
	 * Generates a clean (empty) refresh token cookie for logout or invalidation purposes.
	 *
	 * @return A ResponseCookie with an empty value for the refresh token.
	 */
	@Override
	public ResponseCookie getCleanRefreshTokenCookie() {
		log.info("Generating clean refresh token cookie");
		return ResponseCookie.from(refreshTokenName, "").path("/").maxAge(0).build();
	}

}

