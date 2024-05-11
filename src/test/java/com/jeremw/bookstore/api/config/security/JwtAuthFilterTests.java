package com.jeremw.bookstore.api.config.security;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeremw.bookstore.api.auth.JwtService;
import com.jeremw.bookstore.api.user.User;
import com.jeremw.bookstore.api.user.UserResourceException;
import com.jeremw.bookstore.api.user.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
class JwtAuthFilterTests {

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private MockHttpServletRequest request;

	@Mock
	private MockHttpServletResponse response;

	@Mock
	private FilterChain filterChain;

	@Mock
	private JwtService jwtService;

	@Mock
	private UserService userService;

	@InjectMocks
	private JwtAuthFilter jwtAuthFilter;

	@Test
	void doFilterInternal_ValidToken_ShouldAuthenticateUser()
			throws ServletException, IOException, UserResourceException {
		String token = "validToken";

		User user = User.builder().id(1L).username("username").email("email@domain.com").password("password").build();

		when(jwtService.getJwtFromCookies(any())).thenReturn(token);
		when(jwtService.extractUsername(token)).thenReturn(user.getUsername());

		when(userService.findUserByUsername(user.getUsername())).thenReturn(user);
		when(jwtService.isTokenValid(token, user)).thenReturn(true);
		when(request.getRequestURI()).thenReturn("URI");

		jwtAuthFilter.doFilterInternal(request, response, filterChain);

		verify(filterChain, times(1)).doFilter(request, response);
		User userFromCtx = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		assertEquals(user, userFromCtx);
	}

	@Test
	void doFilterInternal_InvalidTokenSignature_ShouldReturnUnauthorizedResponse()
			throws ServletException, IOException {
		String token = "invalidToken";
		when(jwtService.getJwtFromCookies(any())).thenReturn(token);
		when(jwtService.extractUsername(token)).thenThrow(new SignatureException("Invalid token signature"));
		when(request.getRequestURI()).thenReturn("URI");

		jwtAuthFilter.doFilterInternal(request, response, filterChain);

		verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		verify(response, times(1)).setContentType("application/json");
	}

	@Test
	void doFilterInternal_ExpiredJwtException_ShouldReturnUnauthorizedResponse() throws ServletException, IOException {
		String token = "expiredToken";
		when(jwtService.getJwtFromCookies(any())).thenReturn(token);
		when(jwtService.extractUsername(token)).thenThrow(new ExpiredJwtException(null, null, "Token expired"));
		when(request.getRequestURI()).thenReturn("URI");

		jwtAuthFilter.doFilterInternal(request, response, filterChain);

		verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		verify(response, times(1)).setContentType("application/json");
	}

	@Test
	void doFilterInternal_UserResourceException_ShouldReturnResourceExceptionDTO()
			throws ServletException, IOException, UserResourceException {
		String token = "validToken";
		String username = "testUser";
		when(jwtService.getJwtFromCookies(any())).thenReturn(token);
		when(jwtService.extractUsername(token)).thenReturn(username);

		when(userService.findUserByUsername(username))
				.thenThrow(new UserResourceException("User not found", "User not found", HttpStatus.NOT_FOUND));
		when(request.getRequestURI()).thenReturn("URI");

		jwtAuthFilter.doFilterInternal(request, response, filterChain);

		verify(response, times(1)).setStatus(HttpServletResponse.SC_NOT_FOUND);
		verify(response, times(1)).setContentType("application/json");
	}

}
