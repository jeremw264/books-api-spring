package com.jeremw.bookstore.api.config.security;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeremw.bookstore.api.auth.JwtService;
import com.jeremw.bookstore.api.exception.ResourceExceptionDTO;
import com.jeremw.bookstore.api.user.User;
import com.jeremw.bookstore.api.user.UserResourceException;
import com.jeremw.bookstore.api.user.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter class responsible for authenticating requests based on the presence of a valid JWT token.
 * If a valid token is present, it extracts the username from the token, retrieves the user details
 * from the database, and sets the authentication context. If the token is invalid or expired, it
 * returns an appropriate error response.
 * <p>
 * This filter is applied to each incoming request and intercepts requests to endpoints that require
 * authentication.
 * </p>
 *

 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

	private final ObjectMapper mapper;

	private final JwtService jwtService;

	private final UserService userService;

	/**
	 * Filters the incoming request and performs authentication if a valid JWT token is
	 * present.
	 *
	 * @param request     the HTTP servlet request
	 * @param response    the HTTP servlet response
	 * @param filterChain the filter chain for invoking the next filter in the chain
	 * @throws ServletException if a servlet-specific error occurs while handling the
	 *                          request
	 * @throws IOException      if an I/O error occurs while processing the request
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		log.info("Filtering request. Path: {}, Method: {}", request.getRequestURI(), request.getMethod());

		final String username;
		final String token = jwtService.getJwtFromCookies(request);

		if (token == null || request.getRequestURI().contains("/auth")) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			username = jwtService.extractUsername(token);

			log.info("username : {}", username);

			if ((username != null) && (SecurityContextHolder.getContext().getAuthentication() == null)) {

				log.info("Authenticating user: {}", username);

				User user = userService.findUserByUsername(username);

				if (jwtService.isTokenValid(token, user)) {
					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
							user, null, user.getAuthorities());
					authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				}
			}

			filterChain.doFilter(request, response);

		}
		catch (SignatureException e) {

			ResourceExceptionDTO exceptionDTO = ResourceExceptionDTO.builder()
					.errorCode("IncorrectTokenSignature")
					.errorMessage("Token signature is incorrect, the token is not valid.")
					.status(HttpStatus.UNAUTHORIZED)
					.requestURL(request.getRequestURI())
					.build();

			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setContentType(MediaType.APPLICATION_JSON.toString());
			mapper.writeValue(response.getOutputStream(), exceptionDTO);

		}
		catch (ExpiredJwtException e) {

			ResourceExceptionDTO exceptionDTO = ResourceExceptionDTO.builder()
					.errorCode("ExpiredJwtException")
					.errorMessage("Token expired, the token is not valid.")
					.requestURL(request.getRequestURI())
					.status(HttpStatus.UNAUTHORIZED)
					.build();

			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setContentType(MediaType.APPLICATION_JSON.toString());

			mapper.writeValue(response.getOutputStream(), exceptionDTO);

		}
		catch (UserResourceException e) {
			ResourceExceptionDTO exceptionDTO = ResourceExceptionDTO.builder()
					.errorCode(e.getErrorCode())
					.errorMessage(e.getMessage())
					.requestURL(request.getRequestURI())
					.status(e.getStatus())
					.build();

			response.setStatus(e.getStatus().value());
			response.setContentType(MediaType.APPLICATION_JSON.toString());

			mapper.writeValue(response.getOutputStream(), exceptionDTO);
		}

	}

}

