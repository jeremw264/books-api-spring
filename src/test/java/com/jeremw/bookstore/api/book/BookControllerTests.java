package com.jeremw.bookstore.api.book;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeremw.bookstore.api.book.dto.BookDto;
import com.jeremw.bookstore.api.book.dto.CreateBookForm;
import com.jeremw.bookstore.api.book.dto.UpdateBookForm;
import com.jeremw.bookstore.api.book.util.BookMapper;
import com.jeremw.bookstore.api.user.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class BookControllerTests {
	private final static String BASE_PATH = "/users/1/books";

	private MockMvc mvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private BookService bookService;

	private User user;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(SecurityMockMvcConfigurers.springSecurity())
				.build();

		user = User.builder().id(1L).username("username").email("mail@domain.com").password("password").build();
	}

	@Test
	void getBooks_ShouldReturnListOfBooks() throws Exception {
		Book book1 = Book.builder().id(1L).title("book1").description("description1").author("author1").build();

		Book book2 = Book.builder().id(1L).title("book2").description("description2").author("author2").build();

		List<Book> books = Arrays.asList(book1, book2);

		List<BookDto> expectedBooksDto = BookMapper.INSTANCE.toDtoList(books);

		when(bookService.getBooksByUserId(user.getId())).thenReturn(books);

		MvcResult res = mvc.perform(get(BASE_PATH).with(user(user)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		List<BookDto> bookFromController = objectMapper.readValue(
				res.getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<List<BookDto>>() {
				});

		verify(bookService, times(1)).getBooksByUserId(user.getId());
		assertNotNull(bookFromController);
		assertEquals(2, bookFromController.size());
		assertEquals(expectedBooksDto, bookFromController);
	}

	@Test
	void getBookById_ExistingId_ShouldReturnBookDto() throws Exception {
		Long bookId = 1L;
		Book book = Book.builder().id(1L).title("book1").description("description1").author("author1").build();

		BookDto expectedBook = BookMapper.INSTANCE.toDto(book);

		when(bookService.getBookByIdAndUserId(user.getId(), bookId)).thenReturn(book);

		MvcResult res = mvc.perform(get(BASE_PATH + "/" + bookId).with(user(user)))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		BookDto bookFromController = objectMapper.readValue(res.getResponse().getContentAsString(), BookDto.class);

		verify(bookService, times(1)).getBookByIdAndUserId(user.getId(), bookId);
		assertNotNull(bookFromController);
		assertEquals(expectedBook, bookFromController);
	}

	@Test
	void createBook_ValidCreateBookForm_ShouldReturnCreatedBookDto() throws Exception {
		CreateBookForm createBookForm = CreateBookForm.builder()
				.title("newBook")
				.description("newDescription")
				.author("newAuthor")
				.build();

		Book createdBook = Book.builder().id(1L).title("newBook").description("newDescription").author("newAuthor")
				.build();

		BookDto bookDTOExpected = BookMapper.INSTANCE.toDto(createdBook);

		when(bookService.createBookForUser(user.getId(), createBookForm)).thenReturn(createdBook);

		MvcResult res = mvc
				.perform(post(BASE_PATH).with(user(user))
						.content(new ObjectMapper().writeValueAsString(createBookForm))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		BookDto bookCreated = objectMapper.readValue(res.getResponse().getContentAsString(), BookDto.class);

		verify(bookService, times(1)).createBookForUser(user.getId(), createBookForm);
		assertNotNull(bookCreated);
		assertEquals(bookDTOExpected, bookCreated);
	}

	@Test
	void updateBook_ValidUpdateBookForm_ShouldReturnUpdatedBookDto() throws Exception {
		UpdateBookForm updateBookForm = UpdateBookForm.builder()
				.description("newDescription")
				.author("newAuthor")
				.build();

		Book updatedBook = Book.builder()
				.id(1L)
				.title("book")
				.description("newDescription")
				.author("newAuthor")
				.build();

		BookDto expectedBook = BookMapper.INSTANCE.toDto(updatedBook);

		when(bookService.updateBookByIdAndUserId(user.getId(), updatedBook.getId(), updateBookForm)).thenReturn(updatedBook);

		MvcResult res = mvc
				.perform(patch(BASE_PATH + "/" + updatedBook.getId()).with(user(user))
						.content(objectMapper.writeValueAsString(updateBookForm))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		BookDto bookFromController = objectMapper.readValue(res.getResponse().getContentAsString(), BookDto.class);

		verify(bookService, times(1)).updateBookByIdAndUserId(user.getId(), updatedBook.getId(), updateBookForm);
		assertNotNull(bookFromController);
		assertEquals(expectedBook, bookFromController);
	}

	@Test
	void deleteBook_ExistingId_ShouldReturnNoContent() throws Exception {
		Long bookId = 1L;

		doNothing().when(bookService).deleteBookForUser(user.getId(), bookId);

		mvc.perform(delete(BASE_PATH + "/" + bookId).with(user(user))).andExpect(status().isNoContent());

		verify(bookService, Mockito.times(1)).deleteBookForUser(user.getId(), bookId);
	}

}
