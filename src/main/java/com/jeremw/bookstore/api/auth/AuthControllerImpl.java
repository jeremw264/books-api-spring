package com.jeremw.bookstore.api.auth;

import com.jeremw.bookstore.api.auth.dto.AuthDto;
import com.jeremw.bookstore.api.auth.dto.LoginForm;
import com.jeremw.bookstore.api.auth.dto.RegisterForm;
import com.jeremw.bookstore.api.user.User;
import com.jeremw.bookstore.api.user.UserResourceException;
import com.jeremw.bookstore.api.user.dto.UserDto;
import com.jeremw.bookstore.api.user.util.UserMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Implementation of the {@link AuthController} interface providing endpoints for user
 * authentication operations.
 *
 * <p>
 * This class handles user login, registration, token refresh, and logout operations.
 * </p>
 *

 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

	private final AuthService authService;

	private final JwtService jwtService;

	private final RefreshTokenService refreshTokenService;

	/**
	 * Logs in a user with the provided login form.
	 *
	 * @param loginForm The login form containing the username and password.
	 * @return ResponseEntity containing the user information and authentication tokens as cookies.
	 * @throws UserResourceException If there is an error retrieving user information.
	 * @throws AuthResourceException If there is an error during the authentication process.
	 */
	@Override
	public ResponseEntity<UserDto> login(LoginForm loginForm) throws UserResourceException, AuthResourceException {
		log.info("Logging in user: {}", loginForm.getUsername());
		AuthDto authDTO = authService.login(loginForm);

		ResponseCookie jwtCookie = jwtService.generateJwtCookie(authDTO.getAccessToken());
		ResponseCookie refreshTokenCookie = refreshTokenService.generateRefreshTokenCookie(authDTO.getRefreshToken());

		return ResponseEntity.status(HttpStatus.OK)
				.header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
				.header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
				.body(UserMapper.INSTANCE.toDto(authDTO.getUser()));

	}

	/**
	 * Registers a new user with the provided registration form.
	 *
	 * @param registerForm The registration form containing the username, email, and password.
	 * @return ResponseEntity containing the registered user information.
	 * @throws UserResourceException If there is an error registering the user.
	 */
	@Override
	public ResponseEntity<UserDto> register(RegisterForm registerForm) throws UserResourceException {
		log.info("Register user: {}", registerForm.getUsername());

		User userRegister = authService.register(registerForm);

		return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.INSTANCE.toDto(userRegister));
	}

	/**
	 * Refreshes the authentication token using the refresh token from the request cookies.
	 *
	 * @param request The HTTPServletRequest containing the refresh token as a cookie.
	 * @return ResponseEntity containing the new authentication token as a cookie.
	 * @throws UserResourceException         If there is an error retrieving user information.
	 * @throws RefreshTokenResourceException If there is an error during the token refresh process.
	 */
	@Override
	public ResponseEntity<UserDto> refresh(HttpServletRequest request)
			throws UserResourceException, RefreshTokenResourceException {
		String refreshToken = refreshTokenService.getRefreshTokenFromCookies(request);
		AuthDto authDTO = refreshTokenService.generateNewToken(refreshToken);
		ResponseCookie newJwtCookie = jwtService.generateJwtCookie(authDTO.getAccessToken());
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, newJwtCookie.toString()).build();
	}

	/**
	 * Logs out the user by deleting the refresh token from the request cookies.
	 *
	 * @param request The HTTPServletRequest containing the refresh token as a cookie.
	 * @return ResponseEntity indicating successful logout.
	 * @throws RefreshTokenResourceException If there is an error during the logout process.
	 */
	@Override
	public ResponseEntity<Void> logout(HttpServletRequest request) throws RefreshTokenResourceException {
		log.info("Logging out user");
		String refreshToken = refreshTokenService.getRefreshTokenFromCookies(request);
		if (refreshToken != null) {
			refreshTokenService.deleteByToken(refreshToken);
		}
		ResponseCookie jwtCookie = jwtService.getCleanJwtCookie();
		ResponseCookie refreshTokenCookie = refreshTokenService.getCleanRefreshTokenCookie();
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
				.header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
				.build();
	}

}
