package com.jeremw.bookstore.api.user;

import java.util.List;

import com.jeremw.bookstore.api.user.dto.CreateUserForm;
import com.jeremw.bookstore.api.user.dto.UpdateUserForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;

	@Override
	public List<User> getAllUsers() {
		log.info("Getting all users.");
		return userRepository.findAll();
	}

	@Override
	public User findUserByUsername(String username) throws UserResourceException {
		log.info("Getting user by username: {}", username);
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UserResourceException("UserNotFound", "The user ID is not found in the database.",
						HttpStatus.NOT_FOUND));
	}

	@Override
	public User getUserById(Long userId) throws UserResourceException {
		log.info("Getting user by ID: {}", userId);
		return userRepository.findById(userId)
				.orElseThrow(() -> new UserResourceException("UserNotFound", "The user ID is not found in the database.",
						HttpStatus.NOT_FOUND));

	}

	@Override
	public User createUser(CreateUserForm createUserForm) throws UserResourceException {

		log.info("Creating a new user with username: {}", createUserForm.getUsername());

		User userToCreate = User.builder()
				.username(createUserForm.getUsername())
				.email(createUserForm.getEmail())
				.password(passwordEncoder.encode(createUserForm.getPassword()))
				.build();

		try {
			return userRepository.save(userToCreate);
		}
		catch (DataIntegrityViolationException e) {
			throw new UserResourceException("UserAlreadyExists",
					"The user " + createUserForm.getUsername() + " already exists.", HttpStatus.CONFLICT);
		}
		catch (Exception e) {
			throw new UserResourceException("CreateUserError",
					"Error while creating the user " + createUserForm.getUsername() + ".",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@Override
	public User updateUserById(final Long userId, final UpdateUserForm updateUserForm) throws UserResourceException {
		log.info("Updating user with ID: {}", userId);

		User userDatabase = getUserById(userId);
		String newEmail = updateUserForm.getEmail();
		String newPassword = updateUserForm.getPassword();

		if (newEmail != null && !newEmail.isEmpty()) {
			userDatabase.setEmail(newEmail);
		}

		if (newPassword != null && !newPassword.isEmpty()) {
			userDatabase.setPassword(passwordEncoder.encode(newPassword));
		}

		try {
			return userRepository.save(userDatabase);
		}
		catch (Exception e) {
			throw new UserResourceException("UpdateUserError",
					"Error while updating the user with the ID : " + userId.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void deleteUserById(Long userId) throws UserResourceException {
		log.info("Deleting user with ID: {}", userId);
		try {
			userRepository.delete(getUserById(userId));
		}
		catch (Exception e) {
			throw new UserResourceException("DeleteUserError",
					"Error while deleting the user with the ID : " + userId.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
