package com.jeremw.bookstore.api.book.util;

import com.jeremw.bookstore.api.book.Book;
import com.jeremw.bookstore.api.book.dto.BookDto;
import com.jeremw.bookstore.api.util.BaseMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Mapper(componentModel = "spring")
public interface BookMapper extends BaseMapper<Book, BookDto> {
	BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);
}
