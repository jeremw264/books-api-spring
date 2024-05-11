package com.jeremw.bookstore.api.book;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

	List<Book> findBooksByUserId(Long userId);

	Optional<Book> findByIdAndUserId(Long bookId, Long userId);
}
