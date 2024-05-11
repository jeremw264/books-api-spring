package com.jeremw.bookstore.api.config;

import com.jeremw.bookstore.api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class responsible for configuring various aspects of the application.
 * It includes configuration for authentication, authorization, password encoding, and other utilities.
 * <p>
 * This class defines beans for configuring the authentication manager, user details service,
 * authentication provider and password encoder.
 * </p>
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {

	private final UserRepository userRepository;

	/**
	 * Defines a bean for the user details service.
	 *
	 * @return An instance of UserDetailsService.
	 */
	@Bean
	public UserDetailsService userDetailsService() {
		return username -> this.userRepository.findByUsername(username).get();
	}

	/**
	 * Defines a bean for the authentication provider.
	 *
	 * @return An instance of AuthenticationProvider.
	 */
	@Bean
	public AuthenticationProvider authenticationProvider() {
		final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(this.userDetailsService());
		authenticationProvider.setPasswordEncoder(this.passwordEncoder());
		return authenticationProvider;
	}

	/**
	 * Configures the authentication manager.
	 *
	 * @param config The AuthenticationConfiguration to obtain the authentication manager.
	 * @return The configured AuthenticationManager.
	 * @throws Exception If an error occurs during configuration.
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		log.info("Configuring authentication manager.");
		return config.getAuthenticationManager();
	}

	/**
	 * Configures the password encoder.
	 *
	 * @return The configured PasswordEncoder.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		log.info("Configuring password encoder.");
		return new BCryptPasswordEncoder();
	}
}

