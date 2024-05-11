package com.jeremw.bookstore.api.util;

import java.util.List;

/**
 * Interface for mapping entity objects to DTOs and vice versa.
 *
 * @param <E> Entity type
 * @param <D> DTO type
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
public interface BaseMapper<E, D> {

	/**
	 * Maps an entity object to a DTO.
	 *
	 * @param e The entity object
	 * @return The DTO object
	 */
	D toDto(E e);

	/**
	 * Maps a list of entity objects to a list of DTOs.
	 *
	 * @param eList The list of entity objects
	 * @return The list of DTO objects
	 */
	List<D> toDtoList(List<E> eList);

}

