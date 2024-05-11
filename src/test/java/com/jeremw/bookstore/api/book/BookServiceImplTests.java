package com.jeremw.bookstore.api.book;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jeremw.bookstore.api.book.dto.CreateBookForm;
import com.jeremw.bookstore.api.book.dto.UpdateBookForm;
import com.jeremw.bookstore.api.user.User;
import com.jeremw.bookstore.api.user.UserResourceException;
import com.jeremw.bookstore.api.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@ExtendWith(SpringExtension.class)
class BookServiceImplTests {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private UserService userService;

	@InjectMocks
	private BookServiceImpl bookService;

	private User user;

	@BeforeEach
	void setup() {
		user = User.builder().id(1L).username("username").email("mail@domain.com").password("password").build();
	}

	@Test
	void testGetBookes() {
		List<Book> books = new ArrayList<>();
		when(bookRepository.findBooksByUserId(user.getId())).thenReturn(books);

		List<Book> result = bookService.getBooksByUserId(user.getId());

		assertNotNull(result);
		assertEquals(books, result);
		verify(bookRepository, times(1)).findBooksByUserId(user.getId());
	}

	@Test
	void testGetBookById() throws BookResourceException {
		Long bookId = 1L;

		Book book = Book.builder().id(bookId).build();
		when(bookRepository.findByIdAndUserId(user.getId(), bookId)).thenReturn(Optional.of(book));

		Book result = bookService.getBookByIdAndUserId(user.getId(), bookId);

		verify(bookRepository, times(1)).findByIdAndUserId(user.getId(), bookId);
		assertNotNull(result);
		assertEquals(book, result);
	}

	@Test
	void testGetBookByIdNotFound() {
		Long bookId = 1L;

		when(bookRepository.findByIdAndUserId(user.getId(), bookId)).thenReturn(Optional.empty());

		assertThrows(BookResourceException.class, () -> bookService.getBookByIdAndUserId(user.getId(), bookId));

		verify(bookRepository, times(1)).findByIdAndUserId(user.getId(), bookId);
	}

	@Test
	void testCreateBook() throws BookResourceException, UserResourceException {
		CreateBookForm createBookForm = CreateBookForm.builder()
				.title("newBook")
				.description("newDescription")
				.author("newAuthor")
				.build();

		Book bookToCreate = Book.builder()
				.title(createBookForm.getTitle())
				.description(createBookForm.getDescription())
				.author(createBookForm.getAuthor())
				.user(user)
				.build();

		when(userService.getUserById(user.getId())).thenReturn(user);
		when(bookRepository.save(bookToCreate)).thenReturn(bookToCreate);

		Book result = bookService.createBookForUser(user.getId(), createBookForm);

		assertNotNull(result);
		assertEquals(bookToCreate, result);
		verify(bookRepository, times(1)).save(bookToCreate);
	}

	@Test
	void testCreateBookError() throws UserResourceException {
		CreateBookForm createBookForm = CreateBookForm.builder()
				.title("newBook")
				.description("newDescription")
				.author("newAuthor")
				.build();

		Book bookToCreate = Book.builder()
				.title(createBookForm.getTitle())
				.description(createBookForm.getDescription())
				.author(createBookForm.getAuthor())
				.user(user)
				.build();

		when(userService.getUserById(user.getId())).thenReturn(user);
		when(bookRepository.save(bookToCreate)).thenThrow(RuntimeException.class);

		assertThrows(BookResourceException.class, () -> bookService.createBookForUser(user.getId(), createBookForm));
		verify(bookRepository, times(1)).save(bookToCreate);
	}

	@Test
	void testUpdateBook() throws BookResourceException {
		UpdateBookForm updateBookForm = UpdateBookForm.builder()
				.description("newDescription")
				.author("newAuthor")
				.build();


		Book existingBook = Book.builder()
				.id(1L)
				.title("book1")
				.description("newDescription")
				.author("newAuthor")
				.build();

		when(bookRepository.findByIdAndUserId(user.getId(), existingBook.getId())).thenReturn(Optional.of(existingBook));
		when(bookRepository.save(existingBook)).thenReturn(existingBook);

		Book result = bookService.updateBookByIdAndUserId(user.getId(), existingBook.getId(), updateBookForm);

		assertNotNull(result);
		assertEquals(existingBook, result);
		assertEquals(updateBookForm.getDescription(), existingBook.getDescription());
		assertEquals(updateBookForm.getAuthor(), existingBook.getAuthor());
		verify(bookRepository, times(1)).findByIdAndUserId(user.getId(), existingBook.getId());
		verify(bookRepository, times(1)).save(existingBook);
	}

	@Test
	void testUpdateBookNotFound() {
		Long bookId = 1L;

		UpdateBookForm updateBookForm = UpdateBookForm.builder().build();

		when(bookRepository.findByIdAndUserId(user.getId(), bookId)).thenReturn(Optional.empty());

		assertThrows(BookResourceException.class, () -> bookService.updateBookByIdAndUserId(user.getId(), bookId, updateBookForm));
		verify(bookRepository, times(1)).findByIdAndUserId(user.getId(), bookId);
	}

	@Test
	void testUpdateBookError() {
		UpdateBookForm updateBookForm = UpdateBookForm.builder().build();

		Book existingBook = Book.builder()
				.id(1L)
				.title("book1")
				.description("description")
				.author("author")
				.build();

		when(bookRepository.findByIdAndUserId(user.getId(), existingBook.getId())).thenReturn(Optional.of(existingBook));
		when(bookRepository.save(existingBook)).thenThrow(RuntimeException.class);

		assertThrows(BookResourceException.class, () -> bookService.updateBookByIdAndUserId(user.getId(), existingBook.getId(), updateBookForm));
	}

	@Test
	void testDeleteBook() throws BookResourceException {
		Long bookId = 1L;

		Book existingBook = Book.builder()
				.id(1L)
				.title("book1")
				.description("description")
				.author("author")
				.build();

		when(bookRepository.findByIdAndUserId(user.getId(), bookId)).thenReturn(Optional.of(existingBook));

		bookService.deleteBookForUser(user.getId(), bookId);

		verify(bookRepository, times(1)).findByIdAndUserId(user.getId(), bookId);
		verify(bookRepository, times(1)).delete(existingBook);
	}

	@Test
	void testDeleteBookNotFound() {
		Long bookId = 1L;

		when(bookRepository.findByIdAndUserId(user.getId(), bookId)).thenReturn(Optional.empty());

		assertThrows(BookResourceException.class, () -> bookService.deleteBookForUser(user.getId(), bookId));

		verify(bookRepository, times(1)).findByIdAndUserId(user.getId(), bookId);
	}

	@Test
	void testDeleteBookError() {
		Long bookId = 1L;

		Book existingBook = Book.builder()
				.id(bookId)
				.title("book1")
				.description("description")
				.author("author")
				.build();

		when(bookRepository.findByIdAndUserId(user.getId(), bookId)).thenReturn(Optional.of(existingBook));
		doThrow(RuntimeException.class).when(bookRepository).delete(existingBook);

		assertThrows(BookResourceException.class, () -> bookService.deleteBookForUser(user.getId(), bookId));
		verify(bookRepository, times(1)).delete(existingBook);
		verify(bookRepository, times(1)).findByIdAndUserId(user.getId(), bookId);
	}

}
