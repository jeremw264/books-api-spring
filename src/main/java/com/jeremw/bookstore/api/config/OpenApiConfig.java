package com.jeremw.bookstore.api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * Configuration class for OpenAPI documentation.
 * <p>
 * This class defines the OpenAPI definition for Books-API, including contact information, description,
 * title, version, servers, and security requirements.
 * </p>
 *
 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@OpenAPIDefinition(
		info = @Info(contact = @Contact(name = "Jérémy Woirhaye", email = "jerem.woirhaye@gmail.com"),
				description = "OpenApi documentation for Books-API", title = "OpenApi specification - Books-API",
				version = "1.0"),
		servers = {@Server(description = "Local ENV", url = "http://localhost:3001/api/v1")},
		security = {@SecurityRequirement(name = "jwt")})
public class OpenApiConfig {
}
