package com.jeremw.bookstore.api.auth;


import java.time.Instant;
import java.util.Optional;

import com.jeremw.bookstore.api.auth.dto.AuthDto;
import com.jeremw.bookstore.api.user.User;
import com.jeremw.bookstore.api.user.UserResourceException;
import com.jeremw.bookstore.api.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
class RefreshTokenServiceImplTests {

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@Mock
	private JwtService jwtService;

	@Mock
	private UserService userService;

	@InjectMocks
	private RefreshTokenServiceImpl refreshTokenService;

	private String refreshTokenName;

	@BeforeEach
	void init() {
		refreshTokenName = "refreshTokenName";
		Long refreshTokenExpiration = 300000L;
		ReflectionTestUtils.setField(refreshTokenService, "refreshTokenName", refreshTokenName);
		ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpiration", refreshTokenExpiration);
	}

	@Test
	void testCreateRefreshToken() {
		User user = User.builder().username("testUser").build();

		RefreshToken expectedRefreshToken = RefreshToken.builder()
				.revoked(false)
				.user(user)
				.token("token")
				.expiryDate(Instant.ofEpochSecond(0L))
				.build();

		when(refreshTokenRepository.save(any())).thenReturn(expectedRefreshToken);

		RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

		assertNotNull(refreshToken);
	}

	@Test
	void testVerifyExpirationTokenIsNull() {
		RefreshTokenResourceException exception = assertThrows(RefreshTokenResourceException.class,
				() -> refreshTokenService.verifyExpiration(null));
		assertEquals("NullToken", exception.getErrorCode());
		assertEquals("Token is null", exception.getMessage());
		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
	}

	@Test
	void testVerifyExpirationTokenExpired() {
		RefreshToken expiredToken = RefreshToken.builder()
				.expiryDate(Instant.now().minusMillis(1))
				.user(new User())
				.build();
		doNothing().when(refreshTokenRepository).delete(any());

		RefreshTokenResourceException exception = assertThrows(RefreshTokenResourceException.class,
				() -> refreshTokenService.verifyExpiration(expiredToken));
		assertEquals("ExpiredRefreshToken", exception.getErrorCode());
		assertEquals("Refresh token was expired. Please make a new authentication request", exception.getMessage());
		assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
	}

	@Test
	void testFindByToken() throws RefreshTokenResourceException {
		String tokenValue = "testToken";
		RefreshToken refreshToken = RefreshToken.builder().token(tokenValue).build();
		when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(java.util.Optional.of(refreshToken));

		RefreshToken foundToken = refreshTokenService.findByToken(tokenValue);

		assertNotNull(foundToken);
		assertEquals(refreshToken, foundToken);
		verify(refreshTokenRepository, times(1)).findByToken(tokenValue);
	}

	@Test
	void testFindByTokenTokenNotFound() {
		String tokenValue = "testToken";
		when(refreshTokenRepository.findByToken(tokenValue)).thenReturn(java.util.Optional.empty());

		RefreshTokenResourceException exception = assertThrows(RefreshTokenResourceException.class,
				() -> refreshTokenService.findByToken(tokenValue));
		assertEquals("RefreshTokenNotFound", exception.getErrorCode());
		assertEquals("The refresh token is not found.", exception.getMessage());
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
		verify(refreshTokenRepository, times(1)).findByToken(tokenValue);
	}

	@Test
	void testGenerateNewToken() throws RefreshTokenResourceException, UserResourceException {
		String refreshTokenValue = "testRefreshToken";
		String accessTokenValue = "testAccessToken";

		User user = User.builder().username("testUser").build();

		RefreshToken refreshToken = RefreshToken.builder()
				.token(refreshTokenValue)
				.user(user)
				.expiryDate(Instant.now().plusMillis(10000))
				.build();

		when(userService.findUserByUsername(user.getUsername())).thenReturn(user);
		when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(Optional.of(refreshToken));
		when(jwtService.generateToken(any(User.class))).thenReturn(accessTokenValue);

		AuthDto authDto = refreshTokenService.generateNewToken(refreshTokenValue);

		assertNotNull(authDto);
		assertNotNull(authDto.getUser());
		assertEquals(accessTokenValue, authDto.getAccessToken());
		assertEquals(refreshTokenValue, authDto.getRefreshToken());
		verify(userService, times(1)).findUserByUsername(user.getUsername());
		verify(refreshTokenRepository, times(1)).findByToken(refreshTokenValue);
		verify(jwtService, times(1)).generateToken(any(User.class));
	}

	@Test
	void testGenerateNewTokenTokenNotFound() {
		String refreshTokenValue = "testRefreshToken";
		when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(java.util.Optional.empty());

		RefreshTokenResourceException exception = assertThrows(RefreshTokenResourceException.class,
				() -> refreshTokenService.generateNewToken(refreshTokenValue));
		assertEquals("RefreshTokenNotFound", exception.getErrorCode());
		assertEquals("The refresh token is not found.", exception.getMessage());
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
		verify(refreshTokenRepository, times(1)).findByToken(refreshTokenValue);
	}

	@Test
	void testGenerateNewTokenTokenExpired() {
		String refreshTokenValue = "testRefreshToken";
		RefreshToken expiredToken = RefreshToken.builder()
				.token(refreshTokenValue)
				.user(User.builder().username("testUser").build())
				.expiryDate(Instant.now().minusMillis(1))
				.build();
		when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(java.util.Optional.of(expiredToken));

		RefreshTokenResourceException exception = assertThrows(RefreshTokenResourceException.class,
				() -> refreshTokenService.generateNewToken(refreshTokenValue));
		assertEquals("ExpiredRefreshToken", exception.getErrorCode());
		assertEquals("Refresh token was expired. Please make a new authentication request", exception.getMessage());
		assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
		verify(refreshTokenRepository, times(1)).findByToken(refreshTokenValue);
	}

	@Test
	void testGenerateRefreshTokenCookie() {
		String refreshTokenValue = "testRefreshToken";

		ResponseCookie responseCookie = refreshTokenService.generateRefreshTokenCookie(refreshTokenValue);

		assertNotNull(responseCookie);
		assertEquals(refreshTokenName, responseCookie.getName());
		assertEquals(refreshTokenValue, responseCookie.getValue());
		assertEquals("/", responseCookie.getPath());
		assertTrue(responseCookie.getMaxAge().toMillis() > 0);
		assertTrue(responseCookie.isHttpOnly());
		assertTrue(responseCookie.isSecure());
		assertEquals("None", responseCookie.getSameSite());
	}

	@Test
	void testGetRefreshTokenFromCookies() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		Cookie cookie = new Cookie(refreshTokenName, "testRefreshToken");
		when(request.getCookies()).thenReturn(new Cookie[] {cookie});

		String refreshToken = refreshTokenService.getRefreshTokenFromCookies(request);

		assertEquals("testRefreshToken", refreshToken);
	}

	@Test
	void testGetRefreshTokenFromCookiesNoCookie() {
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getCookies()).thenReturn(null);

		String refreshToken = refreshTokenService.getRefreshTokenFromCookies(request);

		assertEquals("", refreshToken);
	}

	@Test
	void testDeleteByToken() throws RefreshTokenResourceException {
		String refreshTokenValue = "testRefreshToken";
		RefreshToken refreshToken = RefreshToken.builder().token(refreshTokenValue).build();
		when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(java.util.Optional.of(refreshToken));

		refreshTokenService.deleteByToken(refreshTokenValue);

		verify(refreshTokenRepository, times(1)).delete(refreshToken);
		verify(refreshTokenRepository, times(1)).findByToken(refreshTokenValue);
	}

	@Test
	void testDeleteByTokenTokenNotFound() {
		String refreshTokenValue = "testRefreshToken";
		when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(Optional.empty());

		RefreshTokenResourceException exception = assertThrows(RefreshTokenResourceException.class,
				() -> refreshTokenService.deleteByToken(refreshTokenValue));
		assertEquals("RefreshTokenNotFound", exception.getErrorCode());
		assertEquals("The refresh token is not found.", exception.getMessage());
		assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
		verify(refreshTokenRepository, times(1)).findByToken(refreshTokenValue);
	}

	@Test
	void testGetCleanRefreshTokenCookie() {
		ResponseCookie responseCookie = refreshTokenService.getCleanRefreshTokenCookie();

		assertNotNull(responseCookie);
		assertEquals("refreshTokenName", responseCookie.getName());
		assertEquals("", responseCookie.getValue());
		assertEquals("/", responseCookie.getPath());
		assertEquals(0, responseCookie.getMaxAge().toMillis());
	}

}
