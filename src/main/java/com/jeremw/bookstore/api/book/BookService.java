package com.jeremw.bookstore.api.book;

import java.util.List;

import com.jeremw.bookstore.api.book.dto.CreateBookForm;
import com.jeremw.bookstore.api.book.dto.UpdateBookForm;
import com.jeremw.bookstore.api.user.UserResourceException;

import org.springframework.stereotype.Service;

/**
 * This interface defines operations related to book management for a user.
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Service
public interface BookService {

	/**
	 * Retrieves all books associated with a specific user.
	 *
	 * @param userId The ID of the user
	 * @return A list of books associated with the user
	 */
	List<Book> getBooksByUserId(Long userId);

	/**
	 * Retrieves a specific book associated with a user by its ID.
	 *
	 * @param userId The ID of the user
	 * @param bookId The ID of the book
	 * @return The book associated with the user
	 * @throws BookResourceException If the book is not found or not accessible by the user
	 */
	Book getBookByIdAndUserId(Long userId, Long bookId) throws BookResourceException;

	/**
	 * Creates a new book for a user.
	 *
	 * @param userId         The ID of the user
	 * @param createBookForm Form containing information to create the book
	 * @return The created book
	 * @throws BookResourceException If there is an error creating the book
	 */
	Book createBookForUser(Long userId, CreateBookForm createBookForm) throws BookResourceException, UserResourceException;

	/**
	 * Updates an existing book associated with a user by its ID.
	 *
	 * @param userId         The ID of the user
	 * @param bookId         The ID of the book
	 * @param updateBookForm Form containing information to update the book
	 * @return The updated book
	 * @throws BookResourceException If the book is not found or cannot be updated
	 */
	Book updateBookByIdAndUserId(Long userId, Long bookId, UpdateBookForm updateBookForm) throws BookResourceException;

	/**
	 * Deletes a book associated with a user by its ID.
	 *
	 * @param userId The ID of the user
	 * @param bookId The ID of the book to delete
	 * @throws BookResourceException If the book is not found or cannot be deleted
	 */
	void deleteBookForUser(Long userId, Long bookId) throws BookResourceException;

}

