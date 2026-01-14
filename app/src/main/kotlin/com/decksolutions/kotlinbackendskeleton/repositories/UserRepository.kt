package com.decksolutions.kotlinbackendskeleton.repositories


import com.decksolutions.kotlinbackendskeleton.models.entities.User
import com.decksolutions.kotlinbackendskeleton.models.entities.UserEntity
import com.decksolutions.kotlinbackendskeleton.models.entities.Users
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory


/**
 * Repository for user data access operations
 */
class UserRepository {
    private val logger = LoggerFactory.getLogger(UserRepository::class.java)

    /**
     * Finds a user by their email
     *
     * @param email User's email to search for
     * @return Found user or null if not exists
     */
    fun findByEmail(email: String): User? {
        return transaction {
            UserEntity.find { Users.email eq email }.firstOrNull()?.toUser()
        }
    }

    /**
     * Finds a user by their ID
     *
     * @param id User's ID to search for
     * @return Found user or null if not exists
     */
    fun findById(id: Long): User? {
        return transaction {
            UserEntity.findById(id)?.toUser()
        }
    }

    /**
     * Finds a user by their username
     *
     * @param username User's username to search for
     * @return Found user or null if not exists
     */
    fun findByUsername(username: String): User? {
        return transaction {
            UserEntity.find { Users.username eq username }.firstOrNull()?.toUser()
        }
    }

    /**
     * Gets a user's password hash by their email
     *
     * @param email User's email
     * @return Password hash or null if user does not exist
     */
    fun getPasswordHashByEmail(email: String): String? {
        return transaction {
            UserEntity.find { Users.email eq email }.firstOrNull()?.passwordHash
        }
    }

    /**
     * Creates a new user in the database
     *
     * @param email User's email
     * @param username User's username
     * @param passwordHash Password hash
     * @return Created user
     * @throws Exception If email or username already exists
     */
    fun create(email: String, username: String, passwordHash: String): User {
        return transaction {
            // Verify email does not exist
            if (emailExists(email)) {
                throw IllegalArgumentException("Email is already registered")
            }

            // Verify username does not exist
            if (usernameExists(username)) {
                throw IllegalArgumentException("Username is already registered")
            }

            val userEntity = UserEntity.new {
                this.email = email
                this.username = username
                this.passwordHash = passwordHash
            }

            logger.info("User created: ${userEntity.email}")
            userEntity.toUser()
        }
    }

    /**
     * Verifies if an email already exists in the database
     *
     * @param email Email to verify
     * @return true if email exists, false otherwise
     */
    fun emailExists(email: String): Boolean {
        return transaction {
            UserEntity.find { Users.email eq email }.firstOrNull() != null
        }
    }

    /**
     * Verifies if a username already exists in the database
     *
     * @param username Username to verify
     * @return true if username exists, false otherwise
     */
    fun usernameExists(username: String): Boolean {
        return transaction {
            UserEntity.find { Users.username eq username }.firstOrNull() != null
        }
    }

    /**
     * Deletes a user from the database by their ID
     *
     * @param id ID of the user to delete
     * @return true if user was deleted, false if not existed
     */
    fun deleteById(id: Long): Boolean {
        return transaction {
            val userEntity = UserEntity.findById(id)
            if (userEntity != null) {
                logger.info("Deleting user with ID: $id")
                userEntity.delete()
                true
            } else {
                false
            }
        }
    }
}
