package com.jeremw.bookstore.api.book;

import java.net.URI;
import java.util.List;

import com.jeremw.bookstore.api.book.dto.BookDto;
import com.jeremw.bookstore.api.book.dto.CreateBookForm;
import com.jeremw.bookstore.api.book.dto.UpdateBookForm;
import com.jeremw.bookstore.api.book.util.BookMapper;
import com.jeremw.bookstore.api.user.UserResourceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


/**
 * Implementation of the {@link BookController} interface for managing books.
 *
 * <p>
 * This class handles HTTP requests related to book operations, such as fetching,
 * creating, updating, and deleting books.
 * </p>
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookControllerImpl implements BookController {

	private final BookService bookService;

	/**
	 * Retrieves all books associated with a specific user.
	 *
	 * @param userId The ID of the user
	 * @return A ResponseEntity containing a list of books associated with the user
	 */
	@Override
	public ResponseEntity<List<BookDto>> getBooks(Long userId) {
		log.info("Fetching all books.");
		List<BookDto> bookDtos = BookMapper.INSTANCE.toDtoList(bookService.getBooksByUserId(userId));
		return ResponseEntity.status(HttpStatus.OK).body(bookDtos);

	}

	/**
	 * Retrieves a specific book associated with a user by its ID.
	 *
	 * @param userId The ID of the user
	 * @param bookId The ID of the book
	 * @return A ResponseEntity containing the book associated with the user
	 */
	@Override
	public ResponseEntity<BookDto> getBookById(Long userId, Long bookId) throws BookResourceException {
		log.info("Fetching book by ID: {}", bookId);
		BookDto bookDto = BookMapper.INSTANCE.toDto(bookService.getBookByIdAndUserId(userId, bookId));
		return ResponseEntity.status(HttpStatus.OK).body(bookDto);
	}

	/**
	 * Creates a new book for a user.
	 *
	 * @param userId         The ID of the user
	 * @param createBookForm Form containing information to create the book
	 * @return A ResponseEntity containing the created book
	 */
	@Override
	public ResponseEntity<BookDto> createBook(Long userId, CreateBookForm createBookForm) throws BookResourceException, UserResourceException {
		log.info("Creating a new book: {}", createBookForm.getTitle());
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/books").toUriString());
		BookDto bookDto = BookMapper.INSTANCE.toDto(bookService.createBookForUser(userId, createBookForm));
		log.info("Book created successfully. ID: {}", bookDto.getId());
		return ResponseEntity.created(uri).body(bookDto);

	}

	/**
	 * Updates an existing book associated with a user by its ID.
	 *
	 * @param userId         The ID of the user
	 * @param bookId         The ID of the book
	 * @param updateBookForm Form containing information to update the book
	 * @return A ResponseEntity containing the updated book
	 */
	@Override
	public ResponseEntity<BookDto> updateBook(Long userId, Long bookId, UpdateBookForm updateBookForm) throws BookResourceException {
		log.info("Updating book with ID: {}", bookId);
		BookDto bookDto = BookMapper.INSTANCE.toDto(bookService.updateBookByIdAndUserId(userId, bookId, updateBookForm));
		log.info("Book updated successfully. ID: {}", bookDto.getId());
		return ResponseEntity.status(HttpStatus.OK).body(bookDto);

	}

	/**
	 * Deletes a book associated with a user by its ID.
	 *
	 * @param userId The ID of the user
	 * @param bookId The ID of the book to delete
	 * @return A ResponseEntity indicating success or failure of the deletion operation
	 */
	@Override
	public ResponseEntity<Void> deleteBook(Long userId, Long bookId) throws BookResourceException {
		log.info("Deleting book with ID: {}", bookId);
		bookService.deleteBookForUser(userId, bookId);
		log.info("Book deleted successfully. ID: {}", bookId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

	}
}
