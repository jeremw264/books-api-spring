package com.jeremw.bookstore.api.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Data
@Builder
public class CreateBookForm {

	@NotBlank(message = "The title is required to create a new book.")
	@Schema(description = "The title of the new book")
	private String title;

	@NotBlank(message = "The description is required to create a new book.")
	@Schema(description = "The description of the new book")
	private String description;

	@NotBlank(message = "The author is required to create a new book.")
	@Schema(description = "The author of the new book")
	private String author;
}
