package com.decksolutions.kotlinbackendskeleton.services

import org.mindrot.jbcrypt.BCrypt
import org.slf4j.LoggerFactory

/**
 * Service for password hashing and verification
 */
class PasswordService {
    private val logger = LoggerFactory.getLogger(PasswordService::class.java)

    /**
     * Generates a password hash using BCrypt
     *
     * @param password Plain text password
     * @return Password hash
     */
    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    /**
     * Verifies if a password matches its hash
     *
     * @param password Plain text password
     * @param hash Stored hash
     * @return true if the password matches, false otherwise
     */
    fun verifyPassword(password: String, hash: String): Boolean {
        return try {
            BCrypt.checkpw(password, hash)
        } catch (e: Exception) {
            logger.error("Error verifying password", e)
            false
        }
    }
}
