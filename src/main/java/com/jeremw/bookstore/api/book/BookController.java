package com.jeremw.bookstore.api.book;

import java.util.List;

import com.jeremw.bookstore.api.book.dto.BookDto;
import com.jeremw.bookstore.api.book.dto.CreateBookForm;
import com.jeremw.bookstore.api.book.dto.UpdateBookForm;
import com.jeremw.bookstore.api.exception.ResourceExceptionDTO;
import com.jeremw.bookstore.api.user.UserResourceException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This interface defines REST endpoints for book management.
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Tag(name = "Books Endpoint")
@RestController
@RequestMapping("/users/{userId}/books")
public interface BookController {

	/**
	 * Retrieves all books associated with a specific user.
	 *
	 * @param userId The ID of the user
	 * @return A ResponseEntity containing a list of books associated with the user
	 */
	@Operation(summary = "Get all books", description = "Returns a list of all books.")
	@ApiResponse(responseCode = "200", description = "Success")
	@ApiResponse(responseCode = "403", description = "Access denied if the ID parameter in the request does not match the ID of the logged-in user.", content = @Content(schema = @Schema(implementation = ResourceExceptionDTO.class)))
	@GetMapping
	@PreAuthorize("authentication.principal.id == #userId")
	ResponseEntity<List<BookDto>> getBooks(@PathVariable Long userId);

	/**
	 * Retrieves a specific book associated with a user by its ID.
	 *
	 * @param userId The ID of the user
	 * @param bookId The ID of the book
	 * @return A ResponseEntity containing the book associated with the user
	 */
	@Operation(summary = "Get book by ID", description = "Returns an book based on the provided ID.")
	@ApiResponse(responseCode = "200", description = "Success")
	@ApiResponse(responseCode = "404", description = "Book not found", content = @Content(schema = @Schema(implementation = ResourceExceptionDTO.class)))
	@ApiResponse(responseCode = "403", description = "Access denied if the ID parameter in the request does not match the ID of the logged-in user.", content = @Content(schema = @Schema(implementation = ResourceExceptionDTO.class)))
	@GetMapping("/{bookId}")
	@PreAuthorize("authentication.principal.id  == #userId")
	ResponseEntity<BookDto> getBookById(@PathVariable Long userId, @PathVariable Long bookId) throws BookResourceException;

	/**
	 * Creates a new book for a user.
	 *
	 * @param userId         The ID of the user
	 * @param createBookForm Form containing information to create the book
	 * @return A ResponseEntity containing the created book
	 */
	@Operation(summary = "Create a new book", description = "Creates a new book.")
	@ApiResponse(responseCode = "201", description = "Book created successfully")
	@ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ResourceExceptionDTO.class)))
	@ApiResponse(responseCode = "403", description = "Access denied if the ID parameter in the request does not match the ID of the logged-in user.", content = @Content(schema = @Schema(implementation = ResourceExceptionDTO.class)))
	@PostMapping
	@PreAuthorize("authentication.principal.id == #userId")
	ResponseEntity<BookDto> createBook(@PathVariable("userId") Long userId,
			@RequestBody @Parameter(description = "Book data", required = true) @Valid CreateBookForm createBookForm) throws BookResourceException, UserResourceException;

	/**
	 * Updates an existing book associated with a user by its ID.
	 *
	 * @param userId         The ID of the user
	 * @param bookId         The ID of the book
	 * @param updateBookForm Form containing information to update the book
	 * @return A ResponseEntity containing the updated book
	 */
	@Operation(summary = "Update an existing book",
			description = "Updates an existing book based on the provided ID.")
	@ApiResponse(responseCode = "200", description = "Book updated successfully")
	@ApiResponse(responseCode = "404", description = "Book not found", content = @Content(schema = @Schema(implementation = ResourceExceptionDTO.class)))
	@ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ResourceExceptionDTO.class)))
	@ApiResponse(responseCode = "403", description = "Access denied if the ID parameter in the request does not match the ID of the logged-in user.", content = @Content(schema = @Schema(implementation = ResourceExceptionDTO.class)))
	@PatchMapping("/{bookId}")
	@PreAuthorize("authentication.principal.id  == #userId")
	ResponseEntity<BookDto> updateBook(@PathVariable Long userId, @PathVariable Long bookId,
			@Parameter(description = "Updated book data",
					required = true) @Valid @RequestBody UpdateBookForm updateBookForm) throws BookResourceException;

	/**
	 * Deletes a book associated with a user by its ID.
	 *
	 * @param userId The ID of the user
	 * @param bookId The ID of the book to delete
	 * @return A ResponseEntity indicating success or failure of the deletion operation
	 */
	@Operation(summary = "Delete an book by ID", description = "Deletes an book based on the provided ID.")
	@ApiResponse(responseCode = "204", description = "Book deleted successfully")
	@ApiResponse(responseCode = "404", description = "Book not found", content = @Content(schema = @Schema(implementation = ResourceExceptionDTO.class)))
	@ApiResponse(responseCode = "403", description = "Access denied if the ID parameter in the request does not match the ID of the logged-in user.", content = @Content(schema = @Schema(implementation = ResourceExceptionDTO.class)))
	@DeleteMapping("/{bookId}")
	@PreAuthorize("authentication.principal.id == #userId")
	ResponseEntity<Void> deleteBook(@PathVariable Long userId, @PathVariable Long bookId) throws BookResourceException;

}
