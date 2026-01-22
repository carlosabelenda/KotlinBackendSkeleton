package com.decksolutions.kotlinbackendskeleton

import com.decksolutions.kotlinbackendskeleton.repositories.UserRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UserRepositoryTest {

    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
            driver = "org.h2.Driver"
        )
        transaction {
            exec("""
                CREATE TABLE IF NOT EXISTS users (
                    id BIGSERIAL PRIMARY KEY,
                    email VARCHAR(255) NOT NULL UNIQUE,
                    username VARCHAR(100) NOT NULL UNIQUE,
                    password_hash VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
                )
            """.trimIndent())
        }
        userRepository = UserRepository()
    }

    @After
    fun tearDown() {
        transaction {
            exec("DROP TABLE IF EXISTS users")
        }
    }

    @Test
    fun `test create user successfully`() {
        val user = userRepository.create("test@example.com", "testuser", "hashedpassword123")

        assertNotNull(user)
        assertEquals("test@example.com", user.email)
        assertEquals("testuser", user.username)
        assertEquals("hashedpassword123", user.passwordHash)
        assertNotNull(user.id)
    }

    @Test
    fun `test create user with duplicate email throws exception`() {
        userRepository.create("test@example.com", "testuser1", "hashedpassword123")

        assertFailsWith<IllegalArgumentException> {
            userRepository.create("test@example.com", "testuser2", "hashedpassword456")
        }
    }

    @Test
    fun `test create user with duplicate username throws exception`() {
        userRepository.create("test1@example.com", "testuser", "hashedpassword123")

        assertFailsWith<IllegalArgumentException> {
            userRepository.create("test2@example.com", "testuser", "hashedpassword456")
        }
    }

    @Test
    fun `test findByEmail returns user when exists`() {
        userRepository.create("test@example.com", "testuser", "hashedpassword123")

        val user = userRepository.findByEmail("test@example.com")

        assertNotNull(user)
        assertEquals("test@example.com", user.email)
        assertEquals("testuser", user.username)
    }

    @Test
    fun `test findByEmail returns null when not exists`() {
        val user = userRepository.findByEmail("nonexistent@example.com")

        assertNull(user)
    }

    @Test
    fun `test findById returns user when exists`() {
        val createdUser = userRepository.create("test@example.com", "testuser", "hashedpassword123")

        val user = userRepository.findById(createdUser.id)

        assertNotNull(user)
        assertEquals(createdUser.id, user.id)
        assertEquals("test@example.com", user.email)
    }

    @Test
    fun `test findById returns null when not exists`() {
        val user = userRepository.findById(999L)

        assertNull(user)
    }

    @Test
    fun `test findByUsername returns user when exists`() {
        userRepository.create("test@example.com", "testuser", "hashedpassword123")

        val user = userRepository.findByUsername("testuser")

        assertNotNull(user)
        assertEquals("testuser", user.username)
        assertEquals("test@example.com", user.email)
    }

    @Test
    fun `test findByUsername returns null when not exists`() {
        val user = userRepository.findByUsername("nonexistent")

        assertNull(user)
    }

    @Test
    fun `test getPasswordHashByEmail returns hash when user exists`() {
        userRepository.create("test@example.com", "testuser", "hashedpassword123")

        val hash = userRepository.getPasswordHashByEmail("test@example.com")

        assertEquals("hashedpassword123", hash)
    }

    @Test
    fun `test getPasswordHashByEmail returns null when user not exists`() {
        val hash = userRepository.getPasswordHashByEmail("nonexistent@example.com")

        assertNull(hash)
    }

    @Test
    fun `test emailExists returns true when email exists`() {
        userRepository.create("test@example.com", "testuser", "hashedpassword123")

        assertTrue(userRepository.emailExists("test@example.com"))
    }

    @Test
    fun `test emailExists returns false when email not exists`() {
        assertFalse(userRepository.emailExists("nonexistent@example.com"))
    }

    @Test
    fun `test usernameExists returns true when username exists`() {
        userRepository.create("test@example.com", "testuser", "hashedpassword123")

        assertTrue(userRepository.usernameExists("testuser"))
    }

    @Test
    fun `test usernameExists returns false when username not exists`() {
        assertFalse(userRepository.usernameExists("nonexistent"))
    }

    @Test
    fun `test deleteById returns true and deletes user when exists`() {
        val user = userRepository.create("test@example.com", "testuser", "hashedpassword123")

        val result = userRepository.deleteById(user.id)

        assertTrue(result)
        assertNull(userRepository.findById(user.id))
    }

    @Test
    fun `test deleteById returns false when user not exists`() {
        val result = userRepository.deleteById(999L)

        assertFalse(result)
    }
}
