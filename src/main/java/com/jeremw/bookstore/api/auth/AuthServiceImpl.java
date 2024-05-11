package com.jeremw.bookstore.api.auth;

import com.jeremw.bookstore.api.auth.dto.AuthDto;
import com.jeremw.bookstore.api.auth.dto.LoginForm;
import com.jeremw.bookstore.api.auth.dto.RegisterForm;
import com.jeremw.bookstore.api.user.User;
import com.jeremw.bookstore.api.user.UserResourceException;
import com.jeremw.bookstore.api.user.UserService;
import com.jeremw.bookstore.api.user.dto.CreateUserForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * Implementation of the {@link AuthService} interface providing methods for user authentication operations.
 *
 * <p>
 * This class handles user login and registration operations.
 * </p>
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authenticationManager;

	private final UserService userService;

	private final JwtService jwtService;

	private final RefreshTokenService refreshTokenService;

	/**
	 * Performs user login based on the provided login form.
	 *
	 * @param loginForm The login form containing user credentials.
	 * @return An AuthDto containing the user information and access tokens upon
	 * successful login.
	 * @throws AuthResourceException If authentication fails.
	 * @throws UserResourceException If the user is not found.
	 */
	@Override
	public AuthDto login(LoginForm loginForm) throws AuthResourceException, UserResourceException {

		Authentication authentication;
		final User user;

		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword()));
		}
		catch (AuthenticationException e) {
			throw new AuthResourceException("BadCredential", "Bad Credential", HttpStatus.UNAUTHORIZED);
		}
		catch (Exception e) {
			throw new AuthResourceException("LoginError", "Error while user login.", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		user = userService.findUserByUsername(((User) (authentication.getPrincipal())).getUsername());
		String accessToken = jwtService.generateToken(user);
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

		return AuthDto.builder().user(user).accessToken(accessToken).refreshToken(refreshToken.getToken()).build();
	}

	/**
	 * Registers a new user based on the provided registration form.
	 *
	 * @param registerForm The registration form containing user details.
	 * @return An AuthDto containing the user information and access tokens upon
	 * successful registration.
	 */
	@Override
	public User register(RegisterForm registerForm) throws UserResourceException {

		CreateUserForm createUserForm = CreateUserForm.builder()
				.username(registerForm.getUsername())
				.email(registerForm.getEmail())
				.password(registerForm.getPassword())
				.build();

		return userService.createUser(createUserForm);
	}

}

