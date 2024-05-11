package com.jeremw.bookstore.api.config.security;


import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeremw.bookstore.api.exception.ResourceExceptionDTO;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
class AccessDeniedEntryPointTests {
	@Test
	void handle_AccessDeniedException_ShouldReturnForbiddenResponse() throws IOException, ServletException {
		AccessDeniedEntryPoint accessDeniedEntryPoint = new AccessDeniedEntryPoint();
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		AccessDeniedException accessDeniedException = new AccessDeniedException("Access Denied");

		accessDeniedEntryPoint.handle(request, response, accessDeniedException);

		assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

		ResourceExceptionDTO exceptionDTO = new ObjectMapper().readValue(response.getContentAsString(),
				ResourceExceptionDTO.class);

		assertEquals("Forbidden", exceptionDTO.getErrorCode());
		assertEquals("You are not allowed to reach this endpoint.", exceptionDTO.getErrorMessage());
		assertEquals(request.getRequestURL().toString(), exceptionDTO.getRequestURL());
		assertEquals(HttpStatus.FORBIDDEN, exceptionDTO.getStatus());
	}

}