package com.jeremw.bookstore.api.user;

import java.util.List;
import java.util.Optional;

import com.jeremw.bookstore.api.user.dto.CreateUserForm;
import com.jeremw.bookstore.api.user.dto.UpdateUserForm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
class UserServiceTests {

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Test
	void getAllUsers() {
		User user1 = User.builder().id(1L).username("username1").email("email1").password("password1").build();

		User user2 = User.builder().id(2L).username("username2").email("email2").password("password2").build();

		List<User> users = List.of(user1, user2);

		when(userRepository.findAll()).thenReturn(users);

		List<User> usersFromService = userService.getAllUsers();

		assertNotNull(usersFromService);
		assertEquals(users.size(), usersFromService.size());
		assertEquals(users, usersFromService);
		verify(userRepository, times(1)).findAll();
	}

	@Test
	void getUserById() throws UserResourceException {
		User user = User.builder()
				.id(1L)
				.username("username1")
				.email("firstname.lastname@domain.fr")
				.password("password1")
				.build();

		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

		User userFromService = userService.getUserById(user.getId());

		assertNotNull(userFromService);
		assertEquals(user, userFromService);
		verify(userRepository, times(1)).findById(user.getId());
	}

	@Test
	void getUserNotFoundThrowException() {
		Long userId = 1L;

		when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());

		assertThrows(UserResourceException.class, () -> userService.getUserById(userId));
		verify(userRepository, times(1)).findById(userId);
	}

	@Test
	void getUserByUsername() throws UserResourceException {

		User user = User.builder()
				.id(1L)
				.username("username1")
				.email("firstname.lastname@domain.fr")
				.password("password1")
				.build();

		when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

		User userFromService = userService.findUserByUsername(user.getUsername());

		assertNotNull(userFromService);
		assertEquals(user, userFromService);
		verify(userRepository, times(1)).findByUsername(user.getUsername());
	}

	@Test
	void getUserByUsernameNotFoundThrowException() {
		when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());

		assertThrows(UserResourceException.class, () -> userService.findUserByUsername(""));
	}

	@Test
	void createUser() throws UserResourceException {
		User user = User.builder()
				.id(1L)
				.username("username")
				.email("firstname.lastname@domain.fr")
				.password("password")
				.build();

		CreateUserForm createUserForm = CreateUserForm.builder()
				.username(user.getUsername())
				.email(user.getEmail())
				.password(user.getPassword())
				.build();

		User userToSave = User.builder()
				.username(createUserForm.getUsername())
				.email(createUserForm.getEmail())
				.password(createUserForm.getPassword())
				.build();

		when(userRepository.save(userToSave)).thenReturn(user);
		when(passwordEncoder.encode(createUserForm.getPassword())).thenReturn(createUserForm.getPassword());

		User userCreated = userService.createUser(createUserForm);

		assertNotNull(userCreated);
		assertEquals(user, userCreated);

		verify(userRepository, times(1)).save(userToSave);
		verify(passwordEncoder, times(1)).encode(createUserForm.getPassword());
	}

	@Test
	void creatingErrorIfAlreadyExistsTest() {
		User user = User.builder()
				.id(1L)
				.username("username")
				.email("firstname.lastname@domain.fr")
				.password("password")
				.build();

		CreateUserForm createUserForm = CreateUserForm.builder()
				.username(user.getUsername())
				.email(user.getEmail())
				.password(user.getPassword())
				.build();

		User userToSave = User.builder()
				.username(createUserForm.getUsername())
				.email(createUserForm.getEmail())
				.password(createUserForm.getPassword())
				.build();

		when(userRepository.save(userToSave)).thenThrow(DataIntegrityViolationException.class);
		when(passwordEncoder.encode(createUserForm.getPassword())).thenReturn(createUserForm.getPassword());

		assertThrows(UserResourceException.class, () -> userService.createUser(createUserForm));

		verify(userRepository, times(1)).save(userToSave);
		verify(passwordEncoder, times(1)).encode(createUserForm.getPassword());
	}

	@Test
	void testErrorWhileCreatingTheUser() {

		User user = User.builder()
				.id(1L)
				.username("username")
				.email("firstname.lastname@domain.fr")
				.password("password")
				.build();

		CreateUserForm createUserForm = CreateUserForm.builder()
				.username(user.getUsername())
				.email(user.getEmail())
				.password(user.getPassword())
				.build();

		User userToSave = User.builder()
				.username(createUserForm.getUsername())
				.email(createUserForm.getEmail())
				.password(createUserForm.getPassword())
				.build();

		when(userRepository.save(userToSave)).thenThrow(IllegalArgumentException.class);
		when(passwordEncoder.encode(createUserForm.getPassword())).thenReturn(createUserForm.getPassword());

		assertThrows(UserResourceException.class, () -> userService.createUser(createUserForm));

		verify(userRepository, times(1)).save(userToSave);
		verify(passwordEncoder, times(1)).encode(createUserForm.getPassword());
	}

	@Test
	void updateUserById() throws UserResourceException {
		User user = User.builder()
				.id(1L)
				.username("username")
				.email("firstname.lastname@domain.fr")
				.password("password")
				.build();

		UpdateUserForm updateUserForm = UpdateUserForm.builder()
				.email("new.email@domain.fr")
				.password("new_password")
				.build();

		User userUpdatedExpected = User.builder()
				.id(user.getId())
				.username(user.getUsername())
				.email(updateUserForm.getEmail())
				.password(updateUserForm.getPassword())
				.build();

		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(passwordEncoder.encode(updateUserForm.getPassword())).thenReturn(updateUserForm.getPassword());
		when(userRepository.save(userUpdatedExpected)).thenReturn(userUpdatedExpected);

		User userUpdated = userService.updateUserById(user.getId(), updateUserForm);

		assertNotNull(userUpdated);
		assertEquals(userUpdatedExpected, userUpdated);

		verify(userRepository, times(1)).save(userUpdatedExpected);
		verify(userRepository, times(1)).findById(user.getId());
		verify(passwordEncoder, times(1)).encode(updateUserForm.getPassword());
	}

	@Test
	void testUpdateNoExistentUser() {

		Long userId = 1L;

		UpdateUserForm updateUserForm = UpdateUserForm.builder()
				.email("new.email@domain.fr")
				.password("new_password")
				.build();

		when(userRepository.findById(userId)).thenReturn(Optional.empty());
		when(passwordEncoder.encode("password")).thenReturn("password");

		assertThrows(UserResourceException.class, () -> userService.updateUserById(userId, updateUserForm));

		verify(userRepository, times(1)).findById(userId);
	}

	@Test
	void testErrorWhileUpdatingTheUser() {
		User user = User.builder()
				.id(1L)
				.username("username")
				.email("firstname.lastname@domain.fr")
				.password("password")
				.build();

		UpdateUserForm updateUserForm = UpdateUserForm.builder()
				.email("new.email@domain.fr")
				.password("new_password")
				.build();

		User userUpdatedExpected = User.builder()
				.id(user.getId())
				.username(user.getUsername())
				.email(updateUserForm.getEmail())
				.password(updateUserForm.getPassword())
				.build();

		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		doThrow(IllegalArgumentException.class).when(userRepository).save(userUpdatedExpected);
		when(passwordEncoder.encode(updateUserForm.getPassword())).thenReturn(updateUserForm.getPassword());

		assertThrows(UserResourceException.class, () -> userService.updateUserById(user.getId(), updateUserForm));

		verify(userRepository, times(1)).findById(user.getId());
		verify(passwordEncoder, times(1)).encode(updateUserForm.getPassword());
	}

	@Test
	void deleteUserById() throws UserResourceException {
		User userToDelete = User.builder()
				.id(1L)
				.username("username")
				.email("firstname.lastname@domain.fr")
				.password("password")
				.build();

		when(userRepository.findById(userToDelete.getId())).thenReturn(Optional.of(userToDelete));
		doNothing().when(userRepository).delete(userToDelete);

		userService.deleteUserById(userToDelete.getId());

		verify(userRepository, times(1)).delete(userToDelete);
		verify(userRepository, times(1)).findById(userToDelete.getId());
	}

	@Test
	void testDeleteNonexistentUser() {
		User userToDelete = User.builder()
				.id(1L)
				.username("username")
				.email("firstname.lastname@domain.fr")
				.password("password")
				.build();

		when(userRepository.findById(userToDelete.getId())).thenReturn(Optional.empty());

		assertThrows(UserResourceException.class, () -> userService.deleteUserById(userToDelete.getId()));

		verify(userRepository, times(1)).findById(userToDelete.getId());
	}

	@Test
	void testErrorWhileDeletingTheUser() {
		User userToDelete = User.builder()
				.id(1L)
				.username("username")
				.email("firstname.lastname@domain.fr")
				.password("password")
				.build();

		when(userRepository.findById(userToDelete.getId())).thenReturn(Optional.empty());
		doThrow(new IllegalArgumentException()).when(userRepository).delete(userToDelete);

		assertThrows(UserResourceException.class, () -> userService.deleteUserById(userToDelete.getId()));

		verify(userRepository, times(1)).findById(userToDelete.getId());
	}

}