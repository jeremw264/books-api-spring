package com.jeremw.bookstore.api.user;

import java.util.List;

import com.jeremw.bookstore.api.exception.ResourceExceptionDTO;
import com.jeremw.bookstore.api.user.dto.CreateUserForm;
import com.jeremw.bookstore.api.user.dto.UpdateUserForm;
import com.jeremw.bookstore.api.user.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller interface for managing user-related HTTP endpoints.
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Tag(name = "Users Endpoint")
@RestController
@RequestMapping("/users")
public interface UserController {

	/**
	 * Retrieves a list of all users.
	 *
	 * @return ResponseEntity containing a list of UserDto objects representing all users.
	 */
	@Operation(summary = "Retrieve all users")
	@ApiResponse(responseCode = "200", description = "Full content")
	@GetMapping
	ResponseEntity<List<UserDto>> getAllUser();

	/**
	 * Retrieves a user by their unique identifier.
	 *
	 * @param userId The unique identifier of the user.
	 * @return ResponseEntity containing a UserDto object representing the specified user.
	 */
	@Operation(summary = "Find user by ID")
	@ApiResponse(responseCode = "200", description = "The user with the ID in parameter")
	@ApiResponse(responseCode = "404", description = "The user ID is not found in the database.",
			content = @Content(schema = @Schema(implementation = ResourceExceptionDTO.class)))
	@GetMapping("/{userId}")
	ResponseEntity<UserDto> getUserById(@PathVariable Long userId) throws UserResourceException;

	/**
	 * Creates a new user based on the provided form.
	 *
	 * @param createUserForm The form containing information to create a new user.
	 * @return ResponseEntity containing a UserDto object representing the newly created
	 * user.
	 */
	@Operation(summary = "Create a new user")
	@ApiResponse(responseCode = "201", description = "User successfully created.")
	@ApiResponse(responseCode = "409", description = "The user in parameter already exists.",
			content = @Content(schema = @Schema(implementation = ResourceExceptionDTO.class)))
	@ApiResponse(responseCode = "500", description = "Error while creating the user in parameter.",
			content = @Content(schema = @Schema(implementation = ResourceExceptionDTO.class)))
	@PostMapping
	ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserForm createUserForm) throws UserResourceException;

	/**
	 * Updates an existing user based on the provided form.
	 *
	 * @param updateUserForm The form containing information to update an existing user.
	 * @return ResponseEntity containing a UserDto object representing the updated user.
	 */
	@Operation(summary = "Update user by ID")
	@ApiResponse(responseCode = "200", description = "User successfully updated.")
	@ApiResponse(responseCode = "500", description = "Error while updating the user with the ID.",
			content = @Content(schema = @Schema(implementation = ResourceExceptionDTO.class)))
	@PatchMapping("/{userId}")
	ResponseEntity<UserDto> updateUserById(@PathVariable Long userId, @Valid @RequestBody UpdateUserForm updateUserForm)
			throws UserResourceException;

	/**
	 * Deletes a user by their unique identifier.
	 *
	 * @param userId The unique identifier of the user to be deleted.
	 * @return ResponseEntity containing a UserDto object representing the deleted user.
	 */
	@Operation(summary = "Delete user by ID")
	@ApiResponse(responseCode = "204", description = "User successfully deleted.")
	@ApiResponse(responseCode = "500", description = "Error while deleting the user with the ID.",
			content = @Content(schema = @Schema(implementation = ResourceExceptionDTO.class)))
	@DeleteMapping("/{userId}")
	ResponseEntity<Void> deleteUserById(@PathVariable Long userId) throws UserResourceException;

}
