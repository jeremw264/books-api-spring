package com.jeremw.bookstore.api.book.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Data
@Builder
public class BookDto {

	private Long id;

	private String title;

	private String description;

	private String author;

}
