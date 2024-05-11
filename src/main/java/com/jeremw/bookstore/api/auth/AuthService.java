package com.jeremw.bookstore.api.auth;

import com.jeremw.bookstore.api.auth.dto.AuthDto;
import com.jeremw.bookstore.api.auth.dto.LoginForm;
import com.jeremw.bookstore.api.auth.dto.RegisterForm;
import com.jeremw.bookstore.api.user.User;
import com.jeremw.bookstore.api.user.UserResourceException;

import org.springframework.stereotype.Service;

/**
 * Service interface for handling user authentication operations.
 *
 * <p>
 * This service provides methods for user login and registration.
 * </p>
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Service
public interface AuthService {

	/**
	 * Performs user login based on the provided login form.
	 *
	 * @param loginForm The login form containing user credentials.
	 * @return An AuthDto containing the user information and access tokens upon
	 * successful login.
	 * @throws AuthResourceException If authentication fails.
	 * @throws UserResourceException If the user is not found.
	 */
	AuthDto login(LoginForm loginForm) throws AuthResourceException, UserResourceException;

	/**
	 * Registers a new user based on the provided registration form.
	 *
	 * @param registerForm The registration form containing user details.
	 * @return An AuthDto containing the user information and access tokens upon
	 * successful registration.
	 */
	User register(RegisterForm registerForm) throws UserResourceException;

}

