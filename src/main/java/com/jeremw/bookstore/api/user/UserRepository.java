package com.jeremw.bookstore.api.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing user entities using Spring Data JPA.
 * This interface extends {@link org.springframework.data.jpa.repository.JpaRepository}.
 *
 * <p>
 * This repository provides methods for performing CRUD operations on user entities
 * and querying user data from the underlying database.
 * </p>
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * Finds a user by their username.
	 *
	 * @param username The username of the user to find.
	 * @return An Optional containing the user if found, otherwise an empty Optional.
	 */
	Optional<User> findByUsername(String username);

}
