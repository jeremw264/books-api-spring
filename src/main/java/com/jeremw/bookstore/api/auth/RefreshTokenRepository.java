package com.jeremw.bookstore.api.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link RefreshToken} entities. Extends
 * {@link org.springframework.data.jpa.repository.JpaRepository}.
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	/**
	 * Retrieves an optional {@link RefreshToken} entity by its token value.
	 *
	 * @param token The token value of the refresh token to retrieve.
	 * @return An {@link Optional} containing the found {@link RefreshToken}, or an empty
	 * {@link Optional} if not found.
	 */
	Optional<RefreshToken> findByToken(String token);

}

