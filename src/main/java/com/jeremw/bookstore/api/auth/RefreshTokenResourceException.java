package com.jeremw.bookstore.api.auth;

import com.jeremw.bookstore.api.exception.ResourceException;

import org.springframework.http.HttpStatus;

/**
 * Custom exception class for refresh token-related resource exceptions. Extends
 * {@link ResourceException}.
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
public class RefreshTokenResourceException extends ResourceException {

	/**
	 * Constructs a new refresh token resource exception with the specified error code,
	 * error message, and HTTP status.
	 *
	 * @param errorCode    The error code associated with the exception.
	 * @param errorMessage The error message providing details about the exception.
	 * @param status       The HTTP status associated with the exception.
	 */
	public RefreshTokenResourceException(String errorCode, String errorMessage, HttpStatus status) {
		super(errorCode, errorMessage, status);
	}

}

