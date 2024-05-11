package com.jeremw.bookstore.api.config.security;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeremw.bookstore.api.exception.ResourceExceptionDTO;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
class UnauthenticatedEntryPointTests {
	@Test
	void commence_AuthenticationException_ShouldReturnUnauthorizedResponse() throws IOException {
		UnauthenticatedEntryPoint unauthenticatedEntryPoint = new UnauthenticatedEntryPoint();
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		AuthenticationException authException = new AuthenticationException("Authentication Failed") {
		};

		unauthenticatedEntryPoint.commence(request, response, authException);

		assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

		ResourceExceptionDTO exceptionDTO = new ObjectMapper().readValue(response.getContentAsString(),
				ResourceExceptionDTO.class);

		assertEquals("Unauthenticated", exceptionDTO.getErrorCode());
		assertEquals("You are not allowed to reach this endpoint because you are unauthenticated.",
				exceptionDTO.getErrorMessage());
		assertEquals(request.getRequestURL().toString(), exceptionDTO.getRequestURL());
		assertEquals(HttpStatus.UNAUTHORIZED, exceptionDTO.getStatus());
	}

}