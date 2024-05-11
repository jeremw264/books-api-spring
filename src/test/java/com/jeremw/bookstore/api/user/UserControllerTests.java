package com.jeremw.bookstore.api.user;

import static org.junit.jupiter.api.Assertions.*;


import java.nio.charset.StandardCharsets;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeremw.bookstore.api.user.dto.CreateUserForm;
import com.jeremw.bookstore.api.user.dto.UpdateUserForm;
import com.jeremw.bookstore.api.user.dto.UserDto;
import com.jeremw.bookstore.api.user.util.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class UserControllerTests {
	private final static String BASE_PATH = "/users";

	private MockMvc mvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserService userService;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
	}

	@WithMockUser
	@Test
	void getUsers() throws Exception {

		User user1 = User.builder().id(1L).username("username1").email("email1").password("password1").build();

		User user2 = User.builder().id(2L).username("username2").email("email2").password("password2").build();

		List<User> users = List.of(user1, user2);
		List<UserDto> userDTOS = UserMapper.INSTANCE.toDtoList(users);

		when(userService.getAllUsers()).thenReturn(users);

		MvcResult res = mvc.perform(get(BASE_PATH))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		List<UserDto> usersFromController = objectMapper.readValue(
				res.getResponse().getContentAsString(StandardCharsets.UTF_8), new TypeReference<List<UserDto>>() {
				});

		assertNotNull(usersFromController);
		assertEquals(userDTOS, usersFromController);
		verify(userService, times(1)).getAllUsers();
	}

	@WithMockUser
	@Test
	void getUserById() throws Exception {

		User user = User.builder()
				.id(1L)
				.username("username")
				.email("firstname.lastname@domain.fr")
				.password("password")
				.build();

		when(userService.getUserById(user.getId())).thenReturn(user);

		MvcResult res = mvc.perform(get(BASE_PATH + "/" + user.getId().toString()).characterEncoding("UTF-8"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		UserDto userDTO = objectMapper.readValue(res.getResponse().getContentAsString(StandardCharsets.UTF_8),
				UserDto.class);

		assertNotNull(userDTO);
		verify(userService, times(1)).getUserById(user.getId());
		assertEquals(user.getId(), userDTO.getId());
		assertEquals(user.getEmail(), userDTO.getEmail());
		assertEquals(user.getUsername(), userDTO.getUsername());
	}

	@WithMockUser
	@Test
	void createUser() throws Exception {

		User user = User.builder()
				.id(1L)
				.username("username")
				.email("firstname.lastname@domain.fr")
				.password("password")
				.build();

		UserDto userDTOExpected = UserMapper.INSTANCE.toDto(user);

		CreateUserForm createUserForm = CreateUserForm.builder()
				.username(user.getUsername())
				.email(user.getEmail())
				.password(user.getPassword())
				.build();

		when(userService.createUser(createUserForm)).thenReturn(user);

		MvcResult res = mvc
				.perform(post(BASE_PATH).content(new ObjectMapper().writeValueAsString(createUserForm))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		UserDto userCreated = objectMapper.readValue(res.getResponse().getContentAsString(), UserDto.class);

		assertNotNull(userCreated);
		assertEquals(userDTOExpected, userCreated);

		verify(userService, times(1)).createUser(createUserForm);

	}

	@WithMockUser
	@Test
	void updateUser() throws Exception {
		User user = User.builder()
				.id(1L)
				.username("username")
				.email("firstname.lastname@domain.fr")
				.password("password")
				.build();

		UpdateUserForm updateUserForm = UpdateUserForm.builder()
				.email("newemail@domain.fr")
				.password("new_password")
				.build();

		User userUpdated = User.builder()
				.id(user.getId())
				.username(user.getUsername())
				.email(updateUserForm.getEmail())
				.password(updateUserForm.getPassword())
				.build();

		UserDto userDTOExpected = UserMapper.INSTANCE.toDto(userUpdated);

		when(userService.updateUserById(user.getId(),updateUserForm)).thenReturn(userUpdated);

		MvcResult res = mvc
				.perform(patch(BASE_PATH+"/"+user.getId()).content(objectMapper.writeValueAsString(updateUserForm))
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn();

		UserDto userDtoUpdated = objectMapper.readValue(res.getResponse().getContentAsString(), UserDto.class);

		assertNotNull(userDtoUpdated);
		assertEquals(userDTOExpected, userDtoUpdated);

		verify(userService, times(1)).updateUserById(user.getId(),updateUserForm);

	}

	@WithMockUser
	@Test
	void deleteUserById() throws Exception {
		User user = User.builder()
				.id(1L)
				.username("username")
				.email("firstname.lastname@domain.fr")
				.password("password")
				.build();

		doNothing().when(userService).deleteUserById(user.getId());

		mvc.perform(delete(BASE_PATH + "/" + user.getId().toString())).andExpect(status().isNoContent());

		verify(userService, times(1)).deleteUserById(user.getId());
	}

}
