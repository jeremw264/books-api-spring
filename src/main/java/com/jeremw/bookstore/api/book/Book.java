package com.jeremw.bookstore.api.book;

import com.jeremw.bookstore.api.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;


/**
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, updatable = false)
	private String title;

	@Column(nullable = false)
	private String description;

	@Column(nullable = false)
	private String author;

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

}
