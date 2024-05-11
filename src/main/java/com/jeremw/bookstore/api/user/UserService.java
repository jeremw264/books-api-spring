package com.jeremw.bookstore.api.user;

import java.util.List;

import com.jeremw.bookstore.api.user.dto.CreateUserForm;
import com.jeremw.bookstore.api.user.dto.UpdateUserForm;

import org.springframework.stereotype.Service;

/**
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Service
public interface UserService {

	/**
	 * Retrieves a list of all users.
	 *
	 * @return List of User objects representing all users.
	 */
	List<User> getAllUsers();

	User findUserByUsername(String username) throws UserResourceException;

	/**
	 * Retrieves a user by their unique identifier.
	 *
	 * @param userId The unique identifier of the user.
	 * @return User object representing the specified user.
	 * @throws UserResourceException if the user with the given ID is not found.
	 */
	User getUserById(Long userId) throws UserResourceException;

	/**
	 * Creates a new user based on the provided form.
	 *
	 * @param createUserForm The form containing information to create a new user.
	 * @return User object representing the newly created user.
	 * @throws UserResourceException if an error occurs while creating the user.
	 */
	User createUser(CreateUserForm createUserForm) throws UserResourceException;

	/**
	 * Updates an existing user based on the provided form.
	 *
	 * @param userId The unique identifier of user.
	 * @param updateUserForm The form containing information to update an existing user.
	 * @return User object representing the updated user.
	 * @throws UserResourceException if an error occurs while updating the user.
	 */
	User updateUserById(Long userId, UpdateUserForm updateUserForm) throws UserResourceException;

	/**
	 * Deletes a user by their unique identifier.
	 *
	 * @param userId The unique identifier of the user to be deleted.
	 * @throws UserResourceException if an error occurs while deleting the user.
	 */
	void deleteUserById(Long userId) throws UserResourceException;

}

