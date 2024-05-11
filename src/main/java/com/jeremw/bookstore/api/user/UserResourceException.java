package com.jeremw.bookstore.api.user;

import com.jeremw.bookstore.api.exception.ResourceException;

import org.springframework.http.HttpStatus;

/**
 * Custom exception class for handling user-related resource exceptions.
 * Extends ResourceException class.
 * This exception can be thrown when there is an issue related to user resources.
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
public class UserResourceException extends ResourceException {

	/**
	 * Constructs a new UserResourceException with the specified error code, error
	 * message, and HTTP status.
	 *
	 * @param errorCode    The error code associated with the exception.
	 * @param errorMessage The error message providing details about the exception.
	 * @param status       The HTTP status code associated with the exception.
	 */
	public UserResourceException(String errorCode, String errorMessage, HttpStatus status) {
		super(errorCode, errorMessage, status);
	}

}

