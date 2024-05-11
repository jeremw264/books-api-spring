package com.jeremw.bookstore.api.config.security;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeremw.bookstore.api.exception.ResourceExceptionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

/**
 * Configuration class representing an entry point for access denied users.
 *
 * <p>
 * This class handles the access denied scenario and sends a forbidden response with
 * relevant information.
 * </p>
 *

 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Slf4j
@Configuration
public class AccessDeniedEntryPoint implements AccessDeniedHandler {

	/**
	 * Handles the access denied scenario and sends a forbidden response with relevant
	 * information.
	 *
	 * @param request               The HttpServletRequest.
	 * @param response              The HttpServletResponse.
	 * @param accessDeniedException The AccessDeniedException that occurred.
	 * @throws IOException      If an I/O error occurs during response writing.
	 * @throws ServletException If a servlet-specific error occurs.
	 */
	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {

		log.info("Access denied exception occurred while accessing endpoint : {}", request.getRequestURL().toString(),
				accessDeniedException);

		response.setStatus(HttpStatus.FORBIDDEN.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		ResourceExceptionDTO exceptionDTO = ResourceExceptionDTO.builder()
				.errorCode("Forbidden")
				.errorMessage("You are not allowed to reach this endpoint.")
				.requestURL(request.getRequestURL().toString())
				.status(HttpStatus.FORBIDDEN)
				.build();

		new ObjectMapper().writeValue(response.getOutputStream(), exceptionDTO);
	}

}

