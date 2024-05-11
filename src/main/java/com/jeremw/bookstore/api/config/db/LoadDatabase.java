package com.jeremw.bookstore.api.config.db;

import com.jeremw.bookstore.api.user.User;
import com.jeremw.bookstore.api.user.UserService;
import com.jeremw.bookstore.api.user.dto.CreateUserForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to initialize the database with default data.
 *
 * <p>
 * This class defines a CommandLineRunner bean that is executed when the application
 * context is initialized. It creates default users in the database to be used
 * during application runtime.
 * </p>
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class LoadDatabase {

	private final UserService userService;

	/**
	 * Bean definition for initializing the database with default data.
	 *
	 * @return A CommandLineRunner instance that initializes the database.
	 */
	@Bean
	CommandLineRunner initDatabase() {
		return args -> {
			log.info("Init Database");

			CreateUserForm createUserForm = CreateUserForm.builder()
					.username("root")
					.email("root@univ-lille.fr")
					.password("toor")
					.build();

			User user = userService.createUser(createUserForm);

			log.info("Load Database Finished");
		};
	}

}

