package com.jeremw.bookstore.api.auth;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.jeremw.bookstore.api.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@ExtendWith(SpringExtension.class)
class JwtServiceImplTests {

	@InjectMocks
	private JwtServiceImpl jwtService;

	private String accessTokenName;

	private Long accessTokenExpiration;

	private String accessTokenSecret;

	@BeforeEach
	void init() {
		accessTokenName = "accessTokenName";
		accessTokenExpiration = 300000L;
		accessTokenSecret = "accessTokenSecrettttttttttttttttttttttttttttttttttttttt";
		ReflectionTestUtils.setField(jwtService, "accessTokenName", accessTokenName);
		ReflectionTestUtils.setField(jwtService, "accessTokenSecret", accessTokenSecret);
		ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", accessTokenExpiration);
	}

	@Test
	void generateToken() {
		final Map<String, Object> extraClaims = new HashMap<>();
		final User user = User.builder().username("username").build();

		byte[] keyBytes = Decoders.BASE64.decode(accessTokenSecret);
		String tokenExpected = Jwts.builder()
				.setClaims(extraClaims)
				.setSubject(user.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
				.signWith(Keys.hmacShaKeyFor(keyBytes), SignatureAlgorithm.HS256)
				.compact();

		String token = jwtService.generateToken(user);

		assertNotNull(token);
		assertEquals(tokenExpected, token);
	}

	@Test
	void isTokenValid() {
		final User user = User.builder().username("username").build();

		byte[] keyBytes = Decoders.BASE64.decode(accessTokenSecret);
		String token = Jwts.builder()
				.setSubject(user.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
				.signWith(Keys.hmacShaKeyFor(keyBytes), SignatureAlgorithm.HS256)
				.compact();

		assertTrue(jwtService.isTokenValid(token, user));

	}

	@Test
	void isTokenValidIsInvalid() {
		final User user = User.builder().username("username").build();

		byte[] keyBytes = Decoders.BASE64.decode(accessTokenSecret);
		String token = Jwts.builder()
				.setSubject("incorrectUser")
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
				.signWith(Keys.hmacShaKeyFor(keyBytes), SignatureAlgorithm.HS256)
				.compact();

		assertFalse(jwtService.isTokenValid(token, user));
	}

	@Test
	void generateJwtCookie() {
		String jwt = "token";
		ResponseCookie expectedResponseCookie = ResponseCookie.from(accessTokenName, jwt)
				.path("/")
				.maxAge(TimeUnit.SECONDS.toSeconds(accessTokenExpiration))
				.httpOnly(true)
				.secure(true)
				.sameSite("None")
				.build();

		ResponseCookie responseCookie = jwtService.generateJwtCookie(jwt);

		assertNotNull(responseCookie);
		assertEquals(expectedResponseCookie, responseCookie);
		assertEquals(expectedResponseCookie.getName(), responseCookie.getName());
		assertEquals(expectedResponseCookie.getValue(), responseCookie.getValue());
	}

	@Test
	void getJwtFromCookies() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		Cookie cookieExpected = new Cookie(accessTokenName, "value");
		request.setCookies(cookieExpected);

		String cookieValue = jwtService.getJwtFromCookies(request);

		assertNotNull(cookieValue);
		assertEquals(cookieExpected.getValue(), cookieValue);
	}

	@Test
	void getAccessTokenFromCookiesIsNull() {
		MockHttpServletRequest request = new MockHttpServletRequest();

		String cookieValue = jwtService.getJwtFromCookies(request);

		assertNull(cookieValue);
	}

	@Test
	void getCleanJWtCookie() {
		ResponseCookie responseCookieExpected = ResponseCookie.from(accessTokenName, "").path("/").build();

		ResponseCookie responseCookie = jwtService.getCleanJwtCookie();

		assertNotNull(responseCookie);
		assertEquals(responseCookieExpected, responseCookie);
		assertEquals(responseCookieExpected.getName(), responseCookie.getName());
		assertEquals(responseCookieExpected.getValue(), responseCookie.getValue());

	}

}
