package com.jeremw.bookstore.api.auth;


import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.jeremw.bookstore.api.auth.dto.AuthDto;
import com.jeremw.bookstore.api.auth.dto.LoginForm;
import com.jeremw.bookstore.api.auth.dto.RegisterForm;
import com.jeremw.bookstore.api.user.User;
import com.jeremw.bookstore.api.user.UserResourceException;
import com.jeremw.bookstore.api.user.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@ExtendWith(SpringExtension.class)
class AuthControllerImplTests {

	@Mock
	private AuthService authService;

	@Mock
	private JwtService jwtService;

	@Mock
	private RefreshTokenService refreshTokenService;

	@InjectMocks
	private AuthControllerImpl authController;

	@Test
	void testLogin() throws UserResourceException, AuthResourceException {
		LoginForm loginForm = LoginForm.builder().username("testUser").build();

		long accessTokenExpiration = 3600L;
		long refreshTokenExpiration = 6400L;

		AuthDto authDto = new AuthDto();
		authDto.setAccessToken("testAccessToken");
		authDto.setRefreshToken("testRefreshToken");
		authDto.setUser(User.builder().username("testUser").build());

		ResponseCookie jwtCookie = ResponseCookie.from("accessTokenName", authDto.getAccessToken())
				.path("/")
				.maxAge(TimeUnit.SECONDS.toSeconds(accessTokenExpiration))
				.httpOnly(true)
				.secure(true)
				.sameSite("None")
				.build();

		ResponseCookie refreshCookie = ResponseCookie.from("refreshTokenName", authDto.getRefreshToken())
				.path("/")
				.maxAge(TimeUnit.SECONDS.toSeconds(refreshTokenExpiration))
				.httpOnly(true)
				.secure(true)
				.sameSite("None")
				.build();

		when(authService.login(loginForm)).thenReturn(authDto);
		when(jwtService.generateJwtCookie(authDto.getAccessToken())).thenReturn(jwtCookie);
		when(refreshTokenService.generateRefreshTokenCookie(authDto.getRefreshToken())).thenReturn(refreshCookie);

		ResponseEntity<UserDto> responseEntity = authController.login(loginForm);

		assertNotNull(responseEntity);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertNotNull(responseEntity.getHeaders());
		assertTrue(responseEntity.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
		assertEquals(2, responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE).size());

		UserDto userDto = responseEntity.getBody();
		assertNotNull(userDto);
		assertEquals("testUser", userDto.getUsername());
	}

	@Test
	void testRegister() throws UserResourceException {
		RegisterForm registerForm = RegisterForm.builder().username("testUser").build();

		User user = User.builder().username("testUser").build();

		when(authService.register(registerForm)).thenReturn(user);

		ResponseEntity<UserDto> responseEntity = authController.register(registerForm);

		assertNotNull(responseEntity);
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
		assertNotNull(responseEntity.getBody());

		UserDto userDto = responseEntity.getBody();
		assertNotNull(userDto);
		assertEquals("testUser", userDto.getUsername());
		verify(authService, times(1)).register(registerForm);
	}

	@Test
	void testRefresh() throws UserResourceException, RefreshTokenResourceException {
		HttpServletRequest request = mock(HttpServletRequest.class);

		long accessTokenExpiration = 3600L;

		AuthDto authDto = new AuthDto();
		authDto.setAccessToken("testAccessToken");
		authDto.setRefreshToken("testRefreshToken");
		authDto.setUser(User.builder().username("testUser").build());

		ResponseCookie jwtCookie = ResponseCookie.from("accessTokenName", authDto.getAccessToken())
				.path("/")
				.maxAge(TimeUnit.SECONDS.toSeconds(accessTokenExpiration))
				.httpOnly(true)
				.secure(true)
				.sameSite("None")
				.build();

		when(jwtService.generateJwtCookie(authDto.getAccessToken())).thenReturn(jwtCookie);
		when(refreshTokenService.getRefreshTokenFromCookies(request)).thenReturn("testRefreshToken");
		when(refreshTokenService.generateNewToken("testRefreshToken")).thenReturn(authDto);

		ResponseEntity<UserDto> responseEntity = authController.refresh(request);

		assertNotNull(responseEntity);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertNotNull(responseEntity.getHeaders());
		assertTrue(responseEntity.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
		assertNotNull(responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE));
		assertEquals(1, Objects.requireNonNull(responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE)).size());

	}

	@Test
	void testLogout() throws RefreshTokenResourceException {

		HttpServletRequest request = mock(HttpServletRequest.class);
		ResponseCookie expectedJwtCookie = ResponseCookie.from("accessTokenName", "").path("/").build();
		ResponseCookie expectedRefreshCookie = ResponseCookie.from("refreshTokenName", "").path("/").build();

		when(refreshTokenService.getRefreshTokenFromCookies(request)).thenReturn("testRefreshToken");
		when(jwtService.getCleanJwtCookie()).thenReturn(expectedJwtCookie);
		when(refreshTokenService.getCleanRefreshTokenCookie()).thenReturn(expectedRefreshCookie);

		ResponseEntity<Void> responseEntity = authController.logout(request);

		assertNotNull(responseEntity);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertNotNull(responseEntity.getHeaders());
		assertTrue(responseEntity.getHeaders().containsKey(HttpHeaders.SET_COOKIE));
		assertEquals(2, Objects.requireNonNull(responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE)).size());

		verify(refreshTokenService, times(1)).getCleanRefreshTokenCookie();
		verify(refreshTokenService, times(1)).getRefreshTokenFromCookies(request);
		verify(jwtService, times(1)).getCleanJwtCookie();

		assertTrue(Objects.requireNonNull(responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE))
				.contains(expectedJwtCookie.toString()));
		assertTrue(Objects.requireNonNull(responseEntity.getHeaders().get(HttpHeaders.SET_COOKIE))
				.contains(expectedRefreshCookie.toString()));
	}

}
