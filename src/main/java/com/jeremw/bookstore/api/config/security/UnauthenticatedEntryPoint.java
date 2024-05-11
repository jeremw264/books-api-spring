package com.jeremw.bookstore.api.config.security;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeremw.bookstore.api.exception.ResourceExceptionDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * Configuration class representing an entry point for unauthenticated users.
 *
 * <p>
 * This class handles the commencement of authentication for unauthenticated users and
 * sends an unauthorized response with relevant information.
 * </p>
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Slf4j
@Configuration
public class UnauthenticatedEntryPoint implements AuthenticationEntryPoint {

	/**
	 * Handles the commencement of authentication for unauthenticated users.
	 *
	 * @param request       The HttpServletRequest.
	 * @param response      The HttpServletResponse.
	 * @param authException The AuthenticationException that occurred.
	 * @throws IOException If an I/O error occurs during response writing.
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {

		log.info("Access denied exception occurred while accessing endpoint : {}", request.getRequestURL().toString(),
				authException);

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		ResourceExceptionDTO exceptionDTO = ResourceExceptionDTO.builder()
				.errorCode("Unauthenticated")
				.errorMessage("You are not allowed to reach this endpoint because you are unauthenticated.")
				.requestURL(request.getRequestURL().toString())
				.status(HttpStatus.UNAUTHORIZED)
				.build();

		new ObjectMapper().writeValue(response.getOutputStream(), exceptionDTO);
	}

}

