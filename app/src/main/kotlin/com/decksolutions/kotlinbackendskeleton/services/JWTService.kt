package com.decksolutions.kotlinbackendskeleton.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import org.slf4j.LoggerFactory
import java.util.Date

/**
 * Service for JWT token generation and validation
 */
class JWTService(
    private val secret: String,
    private val issuer: String,
    private val audience: String,
    private val expirationTime: Long = 3600000 // 1 hour by default
) {
    private val logger = LoggerFactory.getLogger(JWTService::class.java)
    private val algorithm = Algorithm.HMAC256(secret)

    /**
     * Generates a JWT token for a user
     *
     * @param userId User ID
     * @param email User email
     * @return JWT token as string
     * @throws JWTCreationException If there is an error creating the token
     */
    fun generateToken(userId: Long, email: String): String {
        return try {
            val now = Date()
            val expiration = Date(now.time + expirationTime)

            JWT.create()
                .withIssuer(issuer)
                .withAudience(audience)
                .withSubject(userId.toString())
                .withClaim("email", email)
                .withIssuedAt(now)
                .withExpiresAt(expiration)
                .sign(algorithm)
        } catch (e: JWTCreationException) {
            logger.error("Error generating JWT token", e)
            throw e
        }
    }

    /**
     * Validates a JWT token and extracts the user ID
     *
     * @param token JWT token to validate
     * @return User ID if the token is valid
     * @throws JWTVerificationException If the token is invalid
     */
    fun validateToken(token: String): Long {
        return try {
            val verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .withAudience(audience)
                .build()

            val decoded = verifier.verify(token)
            decoded.subject.toLong()
        } catch (e: JWTVerificationException) {
            logger.error("Error validating JWT token", e)
            throw e
        }
    }

    /**
     * Extracts the user email from a JWT token
     *
     * @param token JWT token
     * @return User email
     * @throws JWTVerificationException If the token is invalid
     */
    fun getEmailFromToken(token: String): String {
        return try {
            val verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .withAudience(audience)
                .build()

            val decoded = verifier.verify(token)
            decoded.getClaim("email").asString()
        } catch (e: JWTVerificationException) {
            logger.error("Error extracting email from JWT token", e)
            throw e
        }
    }
}
