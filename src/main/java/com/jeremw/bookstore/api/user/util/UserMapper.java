package com.jeremw.bookstore.api.user.util;

import com.jeremw.bookstore.api.user.User;
import com.jeremw.bookstore.api.user.dto.UserDto;
import com.jeremw.bookstore.api.util.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper interface for converting between {@link User} entities and {@link UserDto} DTOs.
 * <p>
 * This mapper interface defines methods for mapping user entities to user DTOs and vice versa. It utilizes
 * MapStruct to generate the mapping implementations. The mapper is configured as a Spring component.
 * </p>
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User, UserDto> {

	/**
	 * Singleton instance of the UserMapper interface.
	 */
	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

}
