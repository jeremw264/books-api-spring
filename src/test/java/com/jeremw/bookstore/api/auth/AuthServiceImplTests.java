package com.jeremw.bookstore.api.auth;

import com.jeremw.bookstore.api.auth.dto.AuthDto;
import com.jeremw.bookstore.api.auth.dto.LoginForm;
import com.jeremw.bookstore.api.auth.dto.RegisterForm;
import com.jeremw.bookstore.api.user.User;
import com.jeremw.bookstore.api.user.UserResourceException;
import com.jeremw.bookstore.api.user.UserService;
import com.jeremw.bookstore.api.user.dto.CreateUserForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@ExtendWith(SpringExtension.class)
class AuthServiceImplTests {

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private UserService userService;

	@Mock
	private JwtService jwtService;

	@Mock
	private RefreshTokenService refreshTokenService;

	@InjectMocks
	private AuthServiceImpl authService;

	@Test
	void testLogin() throws AuthResourceException, UserResourceException {
		LoginForm loginForm = LoginForm.builder().username("testUser").password("testPassword").build();

		User user = User.builder().username("testUsername").build();

		RefreshToken refreshToken = RefreshToken.builder().token("testRefreshToken").build();

		Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenReturn(authentication);
		when(userService.findUserByUsername("testUsername")).thenReturn(user);
		when(jwtService.generateToken(user)).thenReturn("testAccessToken");
		when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);

		AuthDto authDto = authService.login(loginForm);

		assertNotNull(authDto);
		assertEquals(user, authDto.getUser());
		assertEquals("testAccessToken", authDto.getAccessToken());
		assertNotNull(authDto.getRefreshToken());
		assertEquals("testRefreshToken", authDto.getRefreshToken());
		verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
		verify(userService, times(1)).findUserByUsername("testUsername");
		verify(jwtService, times(1)).generateToken(user);
		verify(refreshTokenService, times(1)).createRefreshToken(user);
	}

	@Test
	void testLoginWithBadCredentials() {
		LoginForm loginForm = LoginForm.builder().username("testUser").password("badPassword").build();

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenThrow(new AuthenticationException("Bad Credential") {
					@Override
					public String getMessage() {
						return super.getMessage();
					}
				});

		assertThrows(AuthResourceException.class, () -> authService.login(loginForm));
	}

	@Test
	void testLoginWithError() {
		LoginForm loginForm = LoginForm.builder().username("testUser").password("testPassword").build();

		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenThrow(RuntimeException.class);

		assertThrows(AuthResourceException.class, () -> authService.login(loginForm));
		verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
	}

	@Test
	void testRegister() throws UserResourceException {
		RegisterForm registerForm = RegisterForm.builder()
				.username("testUser")
				.email("test@example.com")
				.password("testPassword")
				.build();

		CreateUserForm createUserForm = CreateUserForm.builder()
				.username("testUser")
				.email("test@example.com")
				.password("testPassword")
				.build();

		User registeredUser = User.builder().username("testUsername").build();

		when(userService.createUser(createUserForm)).thenReturn(registeredUser);

		User user = authService.register(registerForm);

		assertNotNull(user);
		assertEquals(registeredUser, user);
		verify(userService, times(1)).createUser(createUserForm);
	}

}
