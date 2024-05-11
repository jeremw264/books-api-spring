package com.jeremw.bookstore.api.auth;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.jeremw.bookstore.api.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

/**
 * Service implementation for handling JSON Web Token (JWT) operations.
 *
 * <p>
 * This service provides methods for extracting, generating, validating, and handling JWT
 * tokens.
 * </p>
 *

 * @author Jérémy Woirhaye
 * @version 1.0
 * @since 11/05/2024
 */
@Slf4j
@Component
public class JwtServiceImpl implements JwtService {

	@Value("${auth.access-token.name}")
	private String accessTokenName;

	@Value("${auth.access-token.secret}")
	private String accessTokenSecret;

	@Value("${auth.access-token.expiration-msec}")
	private Long accessTokenExpiration;

	/**
	 * Extracts the username from the provided JWT token.
	 *
	 * @param token The JWT token from which to extract the username.
	 * @return The username extracted from the JWT token.
	 */
	@Override
	public String extractUsername(String token) {
		log.debug("Extracting username from JWT token");
		return extractClaim(token, Claims::getSubject);
	}

	/**
	 * Generates a JWT token for the provided user.
	 *
	 * @param user The user for whom the JWT token is generated.
	 * @return The generated JWT token.
	 */
	@Override
	public String generateToken(User user) {
		return generateToken(new HashMap<>(), user);
	}

	/**
	 * Checks if the provided JWT token is valid for the specified user.
	 *
	 * @param token The JWT token to validate.
	 * @param user  The user against whom to validate the token.
	 * @return {@code true} if the token is valid for the user, {@code false} otherwise.
	 */
	@Override
	public boolean isTokenValid(String token, User user) {
		log.debug("Validating JWT token for LDAP user");
		final String username = extractUsername(token);
		return username.equals(user.getUsername()) && !isTokenExpired(token);
	}

	/**
	 * Checks if the provided JWT token has expired.
	 *
	 * @param token The JWT token to check for expiration.
	 * @return {@code true} if the token has expired, {@code false} otherwise.
	 */
	private boolean isTokenExpired(String token) {
		log.debug("Checking if JWT token is expired");
		return extractExpiration(token).before(new Date());
	}

	/**
	 * Extracts the expiration date from the provided JWT token.
	 *
	 * @param token The JWT token from which to extract the expiration date.
	 * @return The expiration date of the JWT token.
	 */
	private Date extractExpiration(String token) {
		log.debug("Extracting expiration date from JWT token");
		return extractClaim(token, Claims::getExpiration);
	}

	/**
	 * Generates a JWT token with the provided extra claims and user details.
	 *
	 * @param extraClaims Additional claims to be included in the JWT token.
	 * @param userDetails The user for whom the token is generated.
	 * @return The generated JWT token.
	 */
	private String generateToken(Map<String, Object> extraClaims, User userDetails) {
		return buildToken(extraClaims, userDetails, accessTokenExpiration);
	}

	/**
	 * Generates a ResponseCookie containing the JWT token.
	 *
	 * @param jwt The JWT token to be included in the cookie.
	 * @return A ResponseCookie containing the JWT token.
	 */
	@Override
	public ResponseCookie generateJwtCookie(String jwt) {
		return ResponseCookie.from(accessTokenName, jwt)
				.path("/")
				.maxAge(TimeUnit.SECONDS.toSeconds(accessTokenExpiration))
				.httpOnly(true)
				.secure(true)
				.sameSite("None")
				.build();
	}

	/**
	 * Retrieves the JWT token from cookies in the HttpServletRequest.
	 *
	 * @param request The HttpServletRequest from which to retrieve the JWT token.
	 * @return The JWT token retrieved from cookies, or {@code null} if not found.
	 */
	@Override
	public String getJwtFromCookies(HttpServletRequest request) {
		Cookie cookie = WebUtils.getCookie(request, accessTokenName);
		if (cookie != null) {
			return cookie.getValue();
		}
		else {
			return null;
		}
	}

	/**
	 * Generates a clean (empty) JWT cookie for logout or invalidation purposes.
	 *
	 * @return A ResponseCookie with an empty value for JWT token.
	 */
	@Override
	public ResponseCookie getCleanJwtCookie() {
		log.debug("Clean Cookie access token");
		return ResponseCookie.from(accessTokenName, "").path("/").build();
	}

	/**
	 * Builds a JWT token with the provided extra claims, user details, and expiration
	 * time.
	 *
	 * @param extraClaims           Additional claims to be included in the JWT token.
	 * @param user                  The user for whom the token is generated.
	 * @param accessTokenExpiration The expiration time for the JWT token, in
	 *                              milliseconds.
	 * @return The generated JWT token.
	 */
	private String buildToken(Map<String, Object> extraClaims, User user, long accessTokenExpiration) {
		return Jwts.builder()
				.setClaims(extraClaims)
				.setSubject(user.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
				.signWith(getSecretKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	/**
	 * Extracts a specific claim from the provided JWT token using the given claims
	 * resolver.
	 *
	 * @param token          The JWT token from which to extract the claim.
	 * @param claimsResolver A function to resolve a specific claim from the token's
	 *                       claims.
	 * @param <T>            The type of the claim to be extracted.
	 * @return The extracted claim value.
	 */
	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		log.debug("Extracting claim from JWT token");
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * Extracts all claims from the provided JWT token.
	 *
	 * @param token The JWT token from which to extract all claims.
	 * @return The Claims object containing all claims from the token.
	 */
	private Claims extractAllClaims(String token) {
		log.debug("Extracting all claims from JWT token");
		return Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token).getBody();
	}

	/**
	 * Retrieves the secret key used for signing JWT tokens.
	 *
	 * @return The secret key used for JWT token signing.
	 */
	private Key getSecretKey() {
		log.debug("Get access token secret key.");
		byte[] keyBytes = Decoders.BASE64.decode(accessTokenSecret);
		return Keys.hmacShaKeyFor(keyBytes);
	}

}

