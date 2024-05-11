package com.jeremw.bookstore.api.book;

import java.util.List;

import com.jeremw.bookstore.api.book.dto.CreateBookForm;
import com.jeremw.bookstore.api.book.dto.UpdateBookForm;
import com.jeremw.bookstore.api.user.User;
import com.jeremw.bookstore.api.user.UserResourceException;
import com.jeremw.bookstore.api.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * This class implements the {@link BookService} interface and provides methods for book management.
 * It handles operations such as fetching, creating, updating, and deleting books.
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

	private final BookRepository bookRepository;
	private final UserService userService;


	/**
	 * Retrieves all books associated with a specific user.
	 *
	 * @param userId The ID of the user
	 * @return A list of books associated with the user
	 */
	@Override
	public List<Book> getBooksByUserId(Long userId) {
		log.info("Fetching all books for user id {}", userId);
		return bookRepository.findBooksByUserId(userId);

	}

	/**
	 * Retrieves a specific book associated with a user by its ID.
	 *
	 * @param userId The ID of the user
	 * @param bookId The ID of the book
	 * @return The book associated with the user
	 * @throws BookResourceException If the book is not found or not accessible by the user
	 */
	@Override
	public Book getBookByIdAndUserId(Long userId, Long bookId) throws BookResourceException {
		log.info("Fetching book by ID: {}", bookId);
		return bookRepository.findByIdAndUserId(bookId, userId)
				.orElseThrow(() -> new BookResourceException("BookNotFound", "The book ID is not found in the database.",
						HttpStatus.NOT_FOUND));

	}

	/**
	 * Creates a new book for a user.
	 *
	 * @param userId         The ID of the user
	 * @param createBookForm Form containing information to create the book
	 * @return The created book
	 * @throws BookResourceException If there is an error creating the book
	 */
	@Override
	public Book createBookForUser(Long userId, CreateBookForm createBookForm) throws BookResourceException, UserResourceException {
		log.info("Creating book: {}", createBookForm.getTitle());
		User user = userService.getUserById(userId);
		Book bookToCreate = Book.builder()
				.title(createBookForm.getTitle())
				.description(createBookForm.getDescription())
				.author(createBookForm.getAuthor())
				.user(user)
				.build();

		try {
			Book createdBook = bookRepository.save(bookToCreate);
			log.info("Book created successfully: {}", createdBook.getId());
			return createdBook;
		}
		catch (Exception e) {
			log.error("Error creating book: {}", e.getMessage());
			throw new BookResourceException("CreateBookError", "Error while creating the book '"
					+ createBookForm.getTitle() + "' for user " + user.getUsername() + ".",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Updates an existing book associated with a user by its ID.
	 *
	 * @param userId         The ID of the user
	 * @param bookId         The ID of the book
	 * @param updateBookForm Form containing information to update the book
	 * @return The updated book
	 * @throws BookResourceException If the book is not found or cannot be updated
	 */
	@Override
	public Book updateBookByIdAndUserId(Long userId, Long bookId, UpdateBookForm updateBookForm) throws BookResourceException {
		log.info("Updating book with ID: {}", bookId);

		Book bookDatabase = getBookByIdAndUserId(userId, bookId);

		String description = updateBookForm.getDescription();
		String author = updateBookForm.getAuthor();

		if (description != null && !description.isEmpty()) {
			bookDatabase.setDescription(description);
		}

		if (author != null && !author.isEmpty()) {
			bookDatabase.setAuthor(author);
		}

		try {
			Book updatedBook = bookRepository.save(bookDatabase);
			log.info("Book updated successfully: {}", updatedBook.getId());
			return updatedBook;
		}
		catch (Exception e) {
			log.error("Error updating book: {}", e.getMessage());
			throw new BookResourceException("UpdateBookError", "Error while updating the book with the ID '"
					+ bookId + "' for user Id " + userId + ".",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Deletes a book associated with a user by its ID.
	 *
	 * @param userId The ID of the user
	 * @param bookId The ID of the book to delete
	 * @throws BookResourceException If the book is not found or cannot be deleted
	 */
	@Override
	public void deleteBookForUser(Long userId, Long bookId) throws BookResourceException {
		log.info("Deleting book with ID: {}, for user ID : {}", bookId, userId);
		try {
			bookRepository.delete(getBookByIdAndUserId(userId, bookId));
			log.info("Book deleted successfully: {}", bookId);
		}
		catch (Exception e) {
			log.error("Error deleting book: {}", e.getMessage());
			throw new BookResourceException("DeleteBookError",
					"Error while deleting the book with the ID '" + bookId + "' for user ID " + userId + ".",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
