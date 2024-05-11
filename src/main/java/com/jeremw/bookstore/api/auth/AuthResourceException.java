package com.jeremw.bookstore.api.auth;

import com.jeremw.bookstore.api.exception.ResourceException;

import org.springframework.http.HttpStatus;

/**
 * Exception class representing an authentication-related resource exception.
 *
 * <p>
 * This exception is used to handle errors related to authentication resources.
 * </p>
 *

 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
public class AuthResourceException extends ResourceException {

	/**
	 * Constructs a new authentication resource exception with the specified error code,
	 * error message, and HTTP status.
	 *
	 * @param errorCode    The error code associated with the exception.
	 * @param errorMessage The error message providing details about the exception.
	 * @param status       The HTTP status associated with the exception.
	 */
	public AuthResourceException(String errorCode, String errorMessage, HttpStatus status) {
		super(errorCode, errorMessage, status);
	}

}

