package com.jeremw.bookstore.api.user;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import org.springframework.security.core.GrantedAuthority;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
class UserTests {
	@Test
	void testUserDetailsMethods() {
		User user = User.builder()
				.id(1L)
				.username("testUser")
				.email("test@example.com")
				.password("testPassword")
				.build();

		boolean accountNonExpired = user.isAccountNonExpired();
		boolean accountNonLocked = user.isAccountNonLocked();
		boolean credentialsNonExpired = user.isCredentialsNonExpired();
		boolean isEnabled = user.isEnabled();

		assertTrue(accountNonExpired, "Account should be non-expired");
		assertTrue(accountNonLocked, "Account should be non-locked");
		assertTrue(credentialsNonExpired, "Credentials should be non-expired");
		assertTrue(isEnabled, "User should be enabled");
	}

	@Test
	void testGetAuthorities() {
		User user = User.builder().username("testUser").build();

		Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

		assertNotNull(authorities, "Authorities should not be null");
		assertTrue(authorities.isEmpty(), "Authorities should be empty");
	}

}