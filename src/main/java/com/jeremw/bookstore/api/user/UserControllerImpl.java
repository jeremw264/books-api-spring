package com.jeremw.bookstore.api.user;

import java.net.URI;
import java.util.List;

import com.jeremw.bookstore.api.user.dto.CreateUserForm;
import com.jeremw.bookstore.api.user.dto.UpdateUserForm;
import com.jeremw.bookstore.api.user.dto.UserDto;
import com.jeremw.bookstore.api.user.util.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


/**
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

	private final UserService userService;

	@Override
	public ResponseEntity<List<UserDto>> getAllUser() {
		log.info("Getting all users.");
		List<UserDto> userDtoList = UserMapper.INSTANCE.toDtoList(userService.getAllUsers());
		return ResponseEntity.status(HttpStatus.OK).body(userDtoList);
	}

	@Override
	public ResponseEntity<UserDto> getUserById(Long userId) throws UserResourceException {
		log.info("Getting user by ID: {}", userId);
		UserDto userDto = UserMapper.INSTANCE.toDto(userService.getUserById(userId));
		return ResponseEntity.status(HttpStatus.OK).body(userDto);
	}

	@Override
	public ResponseEntity<UserDto> createUser(CreateUserForm createUserForm) throws UserResourceException {
		log.info("Creating a new user with username: {}", createUserForm.getUsername());

		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/users").toUriString());
		UserDto createdUserDto = UserMapper.INSTANCE.toDto(userService.createUser(createUserForm));

		log.info("User created successfully with username: {}", createUserForm.getUsername());
		return ResponseEntity.created(uri).body(createdUserDto);
	}

	@Override
	public ResponseEntity<UserDto> updateUserById(Long userId, UpdateUserForm updateUserForm) throws UserResourceException {
		log.info("Updating user with ID: {}", userId);

		UserDto updatedUserDto = UserMapper.INSTANCE.toDto(userService.updateUserById(userId, updateUserForm));

		log.info("User updated successfully with ID: {}", userId);
		return ResponseEntity.status(HttpStatus.OK).body(updatedUserDto);
	}

	@Override
	public ResponseEntity<Void> deleteUserById(Long userId) throws UserResourceException {
		log.info("Deleting user with ID: {}", userId);

		userService.deleteUserById(userId);

		log.info("User deleted successfully with ID: {}", userId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}

