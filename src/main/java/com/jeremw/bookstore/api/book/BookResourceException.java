package com.jeremw.bookstore.api.book;

import com.jeremw.bookstore.api.exception.ResourceException;

import org.springframework.http.HttpStatus;

/**
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
public class BookResourceException extends ResourceException {

	/**
	 * Constructs a new ResourceException with the specified error code, error message,
	 * and HTTP status.
	 *
	 * @param errorCode    The unique identifier for the type of error.
	 * @param errorMessage The human-readable error message.
	 * @param status       The HTTP status associated with the exception.
	 */
	public BookResourceException(String errorCode, String errorMessage, HttpStatus status) {
		super(errorCode, errorMessage, status);
	}
}
