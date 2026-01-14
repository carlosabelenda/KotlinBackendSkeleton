package com.decksolutions.kotlinbackendskeleton.services

import com.decksolutions.kotlinbackendskeleton.models.entities.User
import com.decksolutions.kotlinbackendskeleton.repositories.UserRepository
import org.slf4j.LoggerFactory

class UserService(
    private val userRepository: UserRepository,
    private val passwordService: PasswordService,
) {
    private val logger = LoggerFactory.getLogger(com.decksolutions.kotlinbackendskeleton.services.UserService::class.java)

    /**
     * Register a new user
     *
     * @param email User email
     * @param username User username
     * @param password Text plain password
     * @return Created user
     * @throws IllegalArgumentException If the email or the username already exist, or if data is invalid
     */
    fun registerUser(email: String, username: String, password: String): User {
        logger.info("Trying to register the user: $email")

        // Validaciones
        validateEmail(email)
        validateUsername(username)
        validatePassword(password)


        // Verificar que el email no exista
        if (userRepository.emailExists(email)) {
            throw IllegalArgumentException("The email already exists")
        }

        // Verificar que el username no exista
        if (userRepository.usernameExists(username)) {
            throw IllegalArgumentException("The username is already registered")
        }

        // Hash de la contraseña
        val passwordHash = passwordService.hashPassword(password)

        // Crear usuario
        return userRepository.create(email, username, passwordHash)
    }

    /**
     * Searches an user by email
     *
     * @param email User email
     * @return User found or null instead
     */
    fun getUserByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    /**
     * Searches an user by ID
     *
     * @param id User ID
     * @return User found or null instead
     */
    fun getUserById(id: Long): User? {
        return userRepository.findById(id)
    }

    /**
     * Removes an user by ID
     *
     * @param id User ID to eliminate from
     * @return true if the user was successfully removed, false otherwise
     * @throws IllegalArgumentException  If the user does not exist
     */
    fun deleteUser(id: Long): Boolean {
        logger.info("Trying to remove user with ID: $id")

        // Verificar que el usuario existe
        val user = userRepository.findById(id)
        if (user == null) {
            throw IllegalArgumentException("User not found")
        }

        // Eliminar usuario
        val deleted = userRepository.deleteById(id)
        if (deleted) {
            logger.info("User successfully removed: ${user.email}")
        }

        return deleted
    }

    /**
     * Verify email format
     *
     * @param email Email to verify
     * @throws IllegalArgumentException If the email is not valid
     */
    private fun validateEmail(email: String) {
        if (email.isBlank()) {
            throw IllegalArgumentException("The email cannot be empty")
        }

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
        if (!email.matches(emailRegex)) {
            throw IllegalArgumentException("The email format is not valid")
        }
    }

    /**
     * Verify username format
     *
     * @param username Username to verify
     * @throws IllegalArgumentException If the username is not valid
     */
    private fun validateUsername(username: String) {
        if (username.isBlank()) {
            throw IllegalArgumentException("The username cannot be empty")
        }

        if (username.length < 3) {
            throw IllegalArgumentException("The username must have 3 characters at least")
        }

        if (username.length > 50) {
            throw IllegalArgumentException("The username lenght must be under 50")
        }

        val usernameRegex = "^[a-zA-Z0-9_]+$".toRegex()
        if (!username.matches(usernameRegex)) {
            throw IllegalArgumentException("A valid username only contains letters, numbers and underscores")
        }
    }

    /**
     * Verify password format
     *
     * @param password Password to verify
     * @throws IllegalArgumentException If the password is not valid
     */
    private fun validatePassword(password: String) {
        if (password.isBlank()) {
            throw IllegalArgumentException("The password cannot be empty")
        }

        if (password.length < 6) {
            throw IllegalArgumentException("The password must have 6 characters at least")
        }

        if (password.length > 100) {
            throw IllegalArgumentException("The password cannot exceed 100 characters")
        }
    }


}

